package com.github.BeatusL.mlnk.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.TimeUtils
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.github.BeatusL.mlnk.MLNK.Companion.debug
import com.github.BeatusL.mlnk.MLNK.Companion.entityCount
import com.github.BeatusL.mlnk.component.ImageComponent
import com.github.BeatusL.mlnk.component.PhysicsComponent
import com.github.BeatusL.mlnk.component.ScoreComponent
import com.github.BeatusL.mlnk.event.*
import com.github.BeatusL.mlnk.input.*
import com.github.BeatusL.mlnk.system.*
import com.github.quillraven.fleks.World
import ktx.app.KtxScreen
import ktx.assets.disposeSafely
import ktx.box2d.createWorld
import ktx.log.logger
import ktx.math.vec2
import kotlin.random.Random

class GameScreen: KtxScreen {
    private val gStage: Stage = Stage(ExtendViewport(gameStageWidth, gameStageHeight))
    private val uiStage: Stage = Stage(ExtendViewport(uiStageWidth, uiStageHeight))
    private val textureAtlas = TextureAtlas("atlas/GameObj.atlas")
    private val font = BitmapFont(Gdx.files.internal("font.fnt"))
    private val textStyle = Label.LabelStyle(font, Color.BLACK)
    private var lastSpawnTime: Long = 0

    private val oWorld = createWorld(gravity = vec2(0f, 0f)).apply {  // physic (object) world
        autoClearForces = false

    }

    private val rWorld: World = World { // world for render objects
        entityCapacity = entityCount
        inject(gStage)
        inject("UI", uiStage)   // can`t inject both stages without a name
        inject(textureAtlas)
        inject(oWorld)

        componentListener<ScoreComponent.Companion.ScoreComponentListener>()
        componentListener<ImageComponent.Companion.ImageComponentListener>()
        componentListener<PhysicsComponent.Companion.PhysicsComponentListener>()

        system<MoveSystem>()
        system<PhysicsSystem>()
        system<EntitySpawnSystem>()
        system<AnimationSystem>()
        system<RenderSystem>()
        system<ProjectileSystem>()
        system<CollisionSpawnSystem>()
        system<LifeSystem>()
        system<AttackSystem>()
        system<DeadSystem>()
        system<ScoreSystem>()
        if (debug) system<DebugSystem>()
    }
    override fun show() {

        rWorld.systems.forEach { system ->
            if (system is EventListener) {
                gStage.addListener(system)
                uiStage.addListener(system)
            }
        }

        val map = TmxMapLoader().load("map/map.tmx")
        gStage.fire(MapChangeEvent(map))

        createScore()

        spawnEnemy()
        log.debug { "GameScreen shown" }

        Gdx.input.inputProcessor =
            InputMultiplexer(KeyboardProcessor(rWorld, rWorld.mapper()),
                TouchProcessor(rWorld, rWorld.mapper(), rWorld.mapper(), rWorld.mapper()))
    }

    override fun resize(width: Int, height: Int) {
        gStage.viewport.update(width, height, true)
        uiStage.viewport.update(width, height, true)
        log.debug { "View resized" }
    }

    override fun render(delta: Float) {
        rWorld.update(delta.coerceAtMost(1/4f)) // delta cap needed to avoid stuttering
        if (TimeUtils.nanoTime() - lastSpawnTime > spawnInterval) {
            spawnEnemy()
            log.debug { "${rWorld.numEntities.toString()} active entities" }
        }
    }

    override fun dispose() {
        font.disposeSafely()
        gStage.disposeSafely()
        uiStage.disposeSafely()
        textureAtlas.disposeSafely()
        rWorld.dispose()
        oWorld.dispose()
        log.debug { "Resources disposed" }
    }


    private fun spawnEnemy() {
        val type = listOf("B", "M", "S").random()
        val y = gameStageHeight - 1.5f
        val x = Random.nextFloat() * 9

        gStage.fire(ObjCreation(type, Vector2(x, y)))
        lastSpawnTime = TimeUtils.nanoTime()
        log.debug { "enemy spawned at $x:$y" }
    }

    private fun createScore() {
        rWorld.entity {
            add<ScoreComponent> {
                label = Label(text + score.toString(), textStyle)
                location = labelLocation
            }
        }
    }



    companion object {
        const val spawnInterval = 500000000
        const val gameStageWidth = 9f
        const val gameStageHeight = 16f
        const val uiStageWidth = 270f
        const val uiStageHeight = 480f
        private val labelLocation = Vector2(10f, 430f)
        private val log = logger<GameScreen>()
    }
}
