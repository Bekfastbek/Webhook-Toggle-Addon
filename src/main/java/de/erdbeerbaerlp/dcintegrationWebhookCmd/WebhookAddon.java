package de.erdbeerbaerlp.dcintegrationWebhookCmd;

import de.erdbeerbaerlp.dcintegration.common.DiscordIntegration;
import de.erdbeerbaerlp.dcintegration.common.addon.DiscordIntegrationAddon;
import de.erdbeerbaerlp.dcintegration.common.storage.Configuration;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Arrays; // Import Arrays
import java.util.List;

public class WebhookAddon implements DiscordIntegrationAddon {

    @Override
    public void load(DiscordIntegration dc) {
        dc.getJDA().addEventListener(new MyListener());
        DiscordIntegration.LOGGER.info("Webhook Addon loaded");
    }

    @Override
    public void reload() {
        DiscordIntegration.LOGGER.info("Webhook Addon reloaded");
    }

    @Override
    public void unload(DiscordIntegration dc) {
        dc.getJDA().removeEventListener(new MyListener());
        DiscordIntegration.LOGGER.info("Webhook Addon unloaded");
    }

    class MyListener extends ListenerAdapter {
        @Override
        public void onMessageReceived(MessageReceivedEvent event) {
            String[] args = event.getMessage().getContentRaw().split(" ");

            // Check if the command is "!webhook"
            if (args[0].equals("!webhook")) {
                // Fetch admin role IDs from configuration and convert to List
                List<String> adminRoleIDs = Arrays.asList(Configuration.instance().commands.adminRoleIDs);

                // Check if the user has any of the admin roles
                boolean isAdmin = event.getMember().getRoles().stream()
                        .anyMatch(role -> adminRoleIDs.contains(role.getId()));

                // If user is not an admin, send an error message
                if (!isAdmin) {
                    event.getChannel().sendMessage("You do not have permission to use this command.").queue();
                    return;  // Exit the method
                }

                // Access the configuration instance
                boolean enableWebhooks = Configuration.instance().webhook.enable;

                // Toggle the webhook setting
                if (!enableWebhooks) {
                    Configuration.instance().webhook.enable = true;
                    event.getChannel().sendMessage("Webhooks enabled!").queue();
                } else {
                    Configuration.instance().webhook.enable = false;
                    event.getChannel().sendMessage("Webhooks disabled!").queue();
                }
            }
        }
    }
}
