package com.github.BeatusL.mlnk.input


import com.github.BeatusL.mlnk.component.ImageComponent
import com.github.BeatusL.mlnk.component.MoveComponent
import com.github.BeatusL.mlnk.component.PlayerComponent
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.World
import ktx.app.KtxInputAdapter
import kotlin.math.abs
import kotlin.math.round
import kotlin.math.sqrt

class TouchProcessor (
    world: World,
    private val moveCmps: ComponentMapper<MoveComponent>,
    private val playerCmps: ComponentMapper<PlayerComponent>,
    private val imageCmps: ComponentMapper<ImageComponent>
): KtxInputAdapter {
    private var touchX = 0f
    private var touchY = 0f
    private var hyp = 0f
    private val players = world.family(allOf = arrayOf(PlayerComponent::class))


    private fun updatePlayerMovement() {
        players.forEach {
            val player = playerCmps[it]
            val image = imageCmps[it].image
            with(moveCmps[it]) {
                val deltaX = touchX - player.x - image.width / 2f
                val deltaY = touchY - player.y - image.height / 2f
                if (touchX == 1000f || (round(abs(deltaY)) == 0f && round(abs(deltaX)) == 0f)) { // round() for dead zone
                    sin = 0f
                    cos = 0f
                } else {
                    hyp = sqrt(deltaX * deltaX + deltaY * deltaY)
                    sin = deltaY / hyp
                    cos = deltaX / hyp
                }
            }

        }
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        touchX = screenX * inputScale
        touchY = height - screenY * inputScale
        updatePlayerMovement()
        return true
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        touchX = screenX * inputScale
        touchY = height - screenY * inputScale
        updatePlayerMovement()
        return true
    }


    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        touchX = 1000f // impossible in normal conditions
        touchY = 0f
        updatePlayerMovement()
        return true
    }


    companion object {
        const val inputScale = 1 / 80f
        const val height = 16f
    }

}


