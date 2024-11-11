package dev.nikomaru.decrementblock

import cloud.commandframework.annotations.AnnotationParser
import cloud.commandframework.arguments.parser.ParserParameters
import cloud.commandframework.arguments.standard.UUIDArgument.UUIDParser
import cloud.commandframework.bukkit.CloudBukkitCapabilities
import cloud.commandframework.execution.AsynchronousCommandExecutionCoordinator
import cloud.commandframework.kotlin.coroutines.annotations.installCoroutineSupport
import cloud.commandframework.meta.SimpleCommandMeta
import cloud.commandframework.paper.PaperCommandManager
import dev.nikomaru.decrementblock.command.DecrementCommand
import dev.nikomaru.decrementblock.command.LocationParser
import io.leangen.geantyref.TypeToken
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin
import java.util.*


class DecrementBlock : JavaPlugin() {

    companion object {
        lateinit var plugin: DecrementBlock
            private set
    }

    override fun onEnable() {
        // Plugin startup logic
        plugin = this
        setCommand()
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }

    fun setCommand() {
        val commandManager: PaperCommandManager<CommandSender> = PaperCommandManager(
            this,
            AsynchronousCommandExecutionCoordinator.newBuilder<CommandSender>().build(),
            java.util.function.Function.identity(),
            java.util.function.Function.identity()
        )

        if (commandManager.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
            commandManager.registerAsynchronousCompletions()
        }

        val annotationParser = AnnotationParser(commandManager, CommandSender::class.java) {
            SimpleCommandMeta.empty()
        }.installCoroutineSupport()

        val parserRegistry = commandManager.parserRegistry()
        parserRegistry.registerParserSupplier(
            TypeToken.get(Location::class.java)
        ) { _: ParserParameters? -> LocationParser() }

        with(annotationParser) {
            // write your command here
            parse(DecrementCommand())
        }
    }
}