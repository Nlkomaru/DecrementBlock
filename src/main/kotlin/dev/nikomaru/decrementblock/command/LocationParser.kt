package dev.nikomaru.decrementblock.command

import cloud.commandframework.arguments.parser.ArgumentParseResult
import cloud.commandframework.arguments.parser.ArgumentParser
import cloud.commandframework.context.CommandContext
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.*

class LocationParser<C> : ArgumentParser<C, Location> {
    override fun parse(
        commandContext: CommandContext<C & Any>, inputQueue: Queue<String>
    ): ArgumentParseResult<Location> {
        try {
            val sender = commandContext.sender
            val x = inputQueue.poll()?.toDouble()
                ?: return ArgumentParseResult.failure(IllegalArgumentException("x is need to be double"))
            val y = inputQueue.poll()?.toDouble()
                ?: return ArgumentParseResult.failure(IllegalArgumentException("y is null"))
            val z = inputQueue.poll()?.toDouble()
                ?: return ArgumentParseResult.failure(IllegalArgumentException("z is null"))
            return ArgumentParseResult.success(
                Location(
                    if (sender is Player) {
                        sender.world
                    } else {
                        Bukkit.getWorld("world")
                    }, x, y, z
                )
            )
        } catch (e: Exception) {
            return ArgumentParseResult.failure(e)
        }
    }

    override fun getRequestedArgumentCount(): Int {
        return 3
    }

}