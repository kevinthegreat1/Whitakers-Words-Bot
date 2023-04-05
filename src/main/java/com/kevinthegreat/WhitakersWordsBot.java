package com.kevinthegreat;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.util.Collections;

public class WhitakersWordsBot extends ListenerAdapter {
    public static void main(String[] args) {
        if (args.length < 1 || args[0].length() != 72) {
            System.out.println("Please provide the token as the first argument.");
        }
        JDA jda = JDABuilder.createLight(args[0], Collections.emptyList()).addEventListeners(new WhitakersWordsBot()).setActivity(Activity.playing("Type /word")).build();
        jda.updateCommands().addCommands(Commands.slash("word", "Search up a word in Whitaker's Words").addOption(OptionType.STRING, "word", "The word to search up", true)).queue();
    }
}