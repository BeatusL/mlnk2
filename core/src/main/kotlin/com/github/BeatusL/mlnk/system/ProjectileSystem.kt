package com.github.BeatusL.mlnk.system



import com.badlogic.gdx.utils.TimeUtils
import com.github.BeatusL.mlnk.component.ImageComponent
import com.github.BeatusL.mlnk.component.PhysicsComponent
import com.github.BeatusL.mlnk.component.ProjectileComponent
import com.github.BeatusL.mlnk.component.SpawnComponent
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem


@AllOf([ProjectileComponent::class, PhysicsComponent::class])
class ProjectileSystem(
    private val phCmps: ComponentMapper<PhysicsComponent>,
    private val prjCmps: ComponentMapper<ProjectileComponent>,
    private val imCmps: ComponentMapper<ImageComponent>
): IteratingSystem() {

    override fun onTickEntity(entity: Entity) {
        val prjCmp = prjCmps[entity]
        val phCmp = phCmps[entity]
        val height = imCmps[entity].image.height

        if (TimeUtils.nanoTime() - prjCmp.prevTime > 1000000000) {
            prjCmp.prevTime = TimeUtils.nanoTime()
            var x = phCmp.body.position.x
            var y = phCmp.body.position.y


            if (prjCmp.prjType == EntityType.BP) {
                x -= BPWidth
                y += height / 2f
            } else {
                x -= RPWidth
                y -= height / 2f
            }


            world.entity {
                add<SpawnComponent> {
                    this.type = prjCmp.prjType
                    this.location.set(x, y)
                }
            }
            //log.debug { "projectile created at $x:$y" }
        }
    }



    companion object{
        const val BPWidth = 0.3f
        const val RPWidth = 0.25f
    }

}
