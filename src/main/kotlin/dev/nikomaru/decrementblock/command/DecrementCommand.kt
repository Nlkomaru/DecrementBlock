package dev.nikomaru.decrementblock.command

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandMethod
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import dev.nikomaru.decrementblock.DecrementBlock.Companion.plugin
import dev.nikomaru.decrementblock.utils.coroutines.minecraft
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.command.CommandSender
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.abs

@CommandMethod("decrement")
class DecrementCommand {

    @CommandMethod("replace <location1> <location2>")
    suspend fun decrement(
        sender: CommandSender, @Argument("location1") location1: Location, @Argument("location2") location2: Location
    ) {
        withContext(Dispatchers.minecraft) {
            val sum =
                abs((location1.blockX - location2.blockX) * (location1.blockY - location2.blockY) * (location1.blockZ - location2.blockZ)).toLong()
            sender.sendRichMessage("sum: $sum")
            var count = 0L
            val time = System.currentTimeMillis()
            for (x in location1.blockX..location2.blockX) {
                for (y in location1.blockY..location2.blockY) {
                    for (z in location1.blockZ..location2.blockZ) {
                        val block = location1.world.getBlockAt(x, y, z)
                        if (count % (sum / 100) == 0.toLong()) {
                            sender.sendRichMessage("${count / (sum / 100)}%")
                            sender.sendRichMessage("x: $x, y: $y, z: $z")
                        }
                        count++
                        if (block.type != Material.AIR && !block.isDisplayedBlock()) {
                            block.type = Material.GLASS
                        }
                    }
                }
            }
            sender.sendRichMessage("done! ${(System.currentTimeMillis() - time) / 1000}sec")
        }

    }
    @CommandMethod("count <location1> <location2>")
    suspend fun count(
              sender: CommandSender, @Argument("location1") location1: Location, @Argument("location2") location2: Location
    ) {
        val uuid = UUID.randomUUID()
        val file = plugin.dataFolder.resolve("$uuid.csv")
        file.parentFile.mkdirs()
        file.createNewFile()
        val set = hashSetOf<Material>()
        val map = hashMapOf<Int,HashMap<Material, Int>>()
        withContext(Dispatchers.minecraft){
            for (y in location1.blockY .. location2.blockY){
                val blocks = hashMapOf<Material, Int>()
                for (x in location1.blockX .. location2.blockX){
                    for (z in location1.blockZ .. location2.blockZ){
                        val material = location1.world.getBlockAt(x, y, z).type
                        if(!material.isAir){
                            if(blocks[material] == null){
                                set.add(material)
                                blocks[material] = 1
                            }else{
                                blocks[material] = blocks[material]!! + 1
                            }
                        }
                    }
                }
                blocks.toSortedMap()
                map[y] = blocks
            }
        }

        val header = listOf("高さ") + set.map { it.name }.toList()
        val lists = arrayListOf<ArrayList<String>>()

        for (y in location1.blockY .. location2.blockY){
            val list = arrayListOf(y.toString())
            set.forEach{
                list.add(map[y]?.get(it)?.toString() ?: "0")
            }
            lists.add(list)
        }

        csvWriter().open(file){
            writeRow(header)
            writeRows(lists)
        }
        sender.sendRichMessage(file.path)
    }


    fun Block.isDisplayedBlock(): Boolean {
        val target = this
        val world = target.world
        val list = listOf(
            world.getBlockAt(target.x + 1, target.y, target.z),
            world.getBlockAt(target.x - 1, target.y, target.z),
            world.getBlockAt(target.x, target.y + 1, target.z),
            world.getBlockAt(target.x, target.y - 1, target.z),
            world.getBlockAt(target.x, target.y, target.z + 1),
            world.getBlockAt(target.x, target.y, target.z - 1)
        )
        for (block in list) {
            if (block.type.isAir) {
                return true
            }
        }
        return false
    }

}