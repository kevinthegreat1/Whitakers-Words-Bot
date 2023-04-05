package com.kevinthegreat;

import com.google.gson.JsonParser;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

public class WhitakersWordsBot extends ListenerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(WhitakersWordsBot.class);

    public static void main(String[] args) {
        if (args.length < 1 || args[0].length() != 72) {
            LOGGER.error("Please provide the token as the first argument.");
            return;
        }
        JDA jda = JDABuilder.createLight(args[0], Collections.emptyList()).addEventListeners(new WhitakersWordsBot()).setActivity(Activity.playing("Type /word")).build();
        jda.updateCommands().addCommands(Commands.slash("word", "Search up a word in Whitaker's Words").addOption(OptionType.STRING, "word", "The word to search up", true)).queue();
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getName().equals("word")) {
            return;
        }
        event.deferReply().queue();
        String word = event.getOption("word", OptionMapping::getAsString);
        CompletableFuture.supplyAsync(() -> {
            try {
                URL url = new URL("https://latin-words.com/cgi-bin/translate.cgi?query=" + word);
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                return JsonParser.parseReader(new BufferedReader(new InputStreamReader(connection.getInputStream()))).getAsJsonObject().get("message").getAsString();
            } catch (MalformedURLException e) {
                return "That's not a word.";
            } catch (IOException e) {
                return "Something went wrong with the network.";
            } catch (Exception e) {
                return "Something went wrong.";
            }
        }).thenAccept(message -> {
            LOGGER.info("Processed query \"" + word + "\" from user " + event.getUser().getId());
            LOGGER.debug("Response: " + message);
            event.getHook().editOriginal(message).queue();
        });
    }
}
