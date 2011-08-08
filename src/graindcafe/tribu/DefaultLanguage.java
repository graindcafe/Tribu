package graindcafe.tribu;

import java.io.File;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Stack;



import org.bukkit.ChatColor;
import org.bukkit.util.config.Configuration;

public class DefaultLanguage extends Language {
	private static HashMap<String, String> Strings = new HashMap<String, String>() {
		/**
				 * 
				 */
		private static final long serialVersionUID = 9166935722459443352L;
		{
			put("File.DefaultLanguageFile","# This is your default language file \n# You should not edit it !\n# Create another language file (custom.yml) \n# and put 'Default: english' if your default language is english\n");
			put("Sign.Buy", "Buy");
			put("Sign.ToggleSpawner", "Spawn's switch");
			put("Sign.Spawner", "Zombie Spawner");
			put("Sign.HighscoreNames", "Top Names");
			put("Sign.HighscorePoints", "Top Points");
			put("Message.UnknownItem", ChatColor.YELLOW + "Sorry, unknown item");
			put("Message.ZombieSpawnList", ChatColor.GREEN + "%s");
			put("Message.ConfirmDeletion", ChatColor.YELLOW + "Please confirm the deletion of the %s level by redoing the command");
			put("Message.ThisOperationIsNotCancellable", ChatColor.RED + "This operation is not cancellable!");
			put("Message.LevelUnloaded", ChatColor.GREEN + "Level successfully unloaded");
			put("Message.InvalidVote", ChatColor.RED + "Invalid vote");
			put("Message.ThankyouForYourVote", ChatColor.GREEN + "Thankyou for your vote");
			put("Message.YouCannotVoteAtThisTime", ChatColor.RED + "You cannot vote at this time");
			put("Message.LevelLoadedSuccessfully", ChatColor.GREEN + "Level loaded successfully");
			put("Message.LevelIsAlreadyTheCurrentLevel", ChatColor.RED + "Level %s is already the current level");
			put("Message.UnableToSaveLevel", ChatColor.RED + "Unable to save level, try again later");
			put("Message.UnableToLoadLevel", ChatColor.RED + "Unable to load level");
			put("Message.NoLevelLoaded", "No level loaded, type '/tribu load' to load one,");
			put("Message.NoLevelLoaded2", "or '/tribu create' to create a new one,");
			put("Message.TeleportedToDeathSpawn", ChatColor.GREEN + "Teleported to death spawn");
			put("Message.DeathSpawnSet", ChatColor.GREEN + "Death spawn set.");
			put("Message.TeleportedToInitialSpawn", ChatColor.GREEN + "Teleported to initial spawn");
			put("Message.InitialSpawnSet", ChatColor.GREEN + "Initial spawn set.");
			put("Message.UnableToSaveCurrentLevel", ChatColor.RED + "Unable to save current level.");
			put("Message.LevelSaveSuccessful", ChatColor.GREEN + "Level save successful");
			put("Message.LevelCreated", ChatColor.GREEN + "Level " + ChatColor.LIGHT_PURPLE + "%s" + ChatColor.GREEN + " created");
			put("Message.UnableToDeleteLevel", ChatColor.RED + "Unable to delete current level.");
			put("Message.LevelDeleted", ChatColor.GREEN + "Level deleted successfully.");
			put("Message.Levels", ChatColor.GREEN + "Levels: %s");
			put("Message.UnknownLevel", ChatColor.RED + "Unknown level: %s");
			put("Message.MaybeNotSaved", ChatColor.YELLOW + "Maybe you have not saved this level or you have not set anything in.");
			put("Message.ZombieModeEnabled", "Zombie Mode enabled!");
			put("Message.ZombieModeDisabled", "Zombie Mode disabled!");
			put("Message.SpawnpointAdded", ChatColor.GREEN + "Spawnpoint added");
			put("Message.SpawnpointRemoved", ChatColor.GREEN + "Spawnpoint removed");
			put("Message.InvalidSpawnName", ChatColor.RED + "Invalid spawn name");
			put("Message.TeleportedToZombieSpawn", ChatColor.GREEN + "Teleported to zombie spawn " + ChatColor.LIGHT_PURPLE + "%s");
			put("Message.UnableToGiveYouThatItem", "Unable to give you that item...");
			put("Message.PurchaseSuccessfulMoney", ChatColor.GREEN + "Purchase successful. Money: " + ChatColor.DARK_PURPLE + "%s $");
			put("Message.YouDontHaveEnoughMoney", ChatColor.DARK_RED + "You don't have enough money for that!");
			put("Message.MoneyPoints", ChatColor.GREEN + "Money: " + ChatColor.DARK_PURPLE + "%s $" + ChatColor.GREEN + " Points: " + ChatColor.RED
					+ "%s");
			put("Message.GameInProgress", ChatColor.YELLOW + "Game in progress, you will spawn next round");
			put("Message.ZombieHavePrevailed", ChatColor.GREEN + "Zombies have prevailed!");
			put("Message.YouHaveReachedWave", ChatColor.GREEN + "You have reached wave " + ChatColor.LIGHT_PURPLE + "%s");
			put("Message.YouJoined", ChatColor.GREEN + "You joined the human strengths against zombies.");
			put("Message.YouLeft", ChatColor.GREEN + "You left the fight against zombies.");
			put("Message.TribuSignAdded", ChatColor.GREEN+"Tribu sign successfully added.");
			put("Message.TribuSignRemoved", ChatColor.GREEN+"Tribu sign successfully removed.");
			put("Broadcast.MapChosen", ChatColor.GREEN + "Map " + ChatColor.LIGHT_PURPLE + "%s" + ChatColor.GREEN + " has been chosen");
			put("Broadcast.MapVoteStarting", ChatColor.GREEN + "Map vote starting,");
			put("Broadcast.Type", ChatColor.GREEN + "Type ");
			put("Broadcast.SlashVoteForMap", ChatColor.GOLD + "'/tribu vote %s'" + ChatColor.GREEN + " for map " + ChatColor.LIGHT_PURPLE + "%s");
			put("Broadcast.VoteClosingInSeconds", ChatColor.GREEN + "Vote closing in " + ChatColor.LIGHT_PURPLE + "%s" + ChatColor.GREEN + " seconds");
			put("Broadcast.StartingWave", ChatColor.GREEN + "Starting wave " + ChatColor.LIGHT_PURPLE + "%s" + ChatColor.GREEN + ", "
					+ ChatColor.LIGHT_PURPLE + "%s" + ChatColor.GREEN + " Zombies @ " + ChatColor.LIGHT_PURPLE + "%s" + ChatColor.GREEN + " health");
			put("Broadcast.Wave", ChatColor.GREEN + "Wave " + ChatColor.LIGHT_PURPLE + "%s" + ChatColor.GREEN + " starting in "
					+ ChatColor.LIGHT_PURPLE + "%s" + ChatColor.GREEN + " seconds.");
			put("Broadcast.WaveComplete", ChatColor.GREEN + "Wave Complete");
			put("Info.LevelFound", "%s levels found");
			put("Info.Enable", "Starting Tribu by Graindcafe, original author : samp20");
			put("Info.Disable", "Stopping Tribu");
			put("Info.LevelSaved", "Level saved");
			put("Info.ChosenLanguage", "Chosen language : %s (default). Provided by : %s.");
			put("Info.LevelFolderDoesntExist", "Level folder doesn't exist");
			put("Warning.AllSpawnsCurrentlyUnloaded", "All zombies spawns are currently unloaded.");
			put("Warning.UnableToSaveLevel", "Unable to save level");
			put("Warning.ThisCommandCannotBeUsedFromTheConsole", "This command cannot be used from the console");
			put("Warning.IOErrorOnFileDelete", "IO error on file delete");
			put("Warning.LanguageFileOutdated", "Your current language file is outdated");
			put("Warning.LanguageFileMissing", "The chosen language file is missing");
			put("Warning.UnableToAddSign", "Unable to add sign, maybe you've changed your locales, or signs' tags.");
			put("Warning.UnknownFocus",
					"The string given for the configuration Zombies.Focus is not recognized : %s . It could be 'None','Nearest','Random','DeathSpawn','InitialSpawn'.");
			put("Severe.WorldInvalidFileVersion", "World invalid file version");
			put("Severe.WorldDoesntExist", "World doesn't exist");
			put("Severe.ErrorDuringLevelLoading", "Error during level loading : %s");
			put("Severe.ErrorDuringLevelSaving", "Error during level saving : %s");
			put("Severe.Exception", "Exception: %s");
		}
	};
	public static void  saveDefaultLanguage(String fileName)
	{
		File dir = new File(Constants.languagesFolder);
		if (!dir.exists()) {
			String[] languageFolders = Constants.languagesFolder.split("/");
			String tmplevelFolder = "";
			for (byte i = 0; i < languageFolders.length; i++) {
				tmplevelFolder = tmplevelFolder.concat(languageFolders[i] + java.io.File.separatorChar);
				dir = new File(tmplevelFolder);
				dir.mkdir();
			}
		}
		if(fileName.substring(fileName.length()-3) != "yml")
			fileName+=".yml";
		Configuration f=new Configuration(new File(Constants.languagesFolder+fileName));
		f.setProperty("Author","Graindcafe");
		f.setProperty("Version", Constants.LanguageFileVersion);
		for(Entry<String, String> e : Strings.entrySet())
		{
			f.setProperty(e.getKey(), e.getValue());
		}
		
		f.setHeader("# This is the plugin default language file \n# You should not edit it ! All changes will be undone !\n# Create another language file (custom.yml) \n# and put 'Default: english' if your default language is english\n");
		f.save();
	}
	public static boolean checkLanguage(Language l) {
		while(!l.isDefault())
			l=l.getDefault();
		boolean retour = true;
		Configuration lFile = l.getFile();
		String value;
		Stack<String> todo = new Stack<String>();
		for (String key : Strings.keySet()) {
			value = lFile.getString(key, null);
			if (value == null) {
				todo.push(key);
			}
		}
		String header = l.get("File.DefaultLanguageFile"); 
		if (lFile.getInt("Version", 0) != Constants.LanguageFileVersion) {
			retour = false;
			header += "# " + Strings.get("Warning.LanguageFileOutdated") + "\n";
		}
		if (todo.isEmpty())
			header += "# Your language file is complete\n";
		else {
			retour = false;
			header += "# Translations to do in this language file\n";
		}
		while (!todo.isEmpty())
			header += "# - " + todo.peek()+": "+Strings.get(todo.pop()) + "\n";
		lFile.setHeader(header);
		lFile.save();
		return retour;
	}

	@Override
	public String get(String key) {
		return Strings.get(key);
	}

	@Override
	public byte getVersion() {
		return Constants.LanguageFileVersion;
	}

	@Override
	public String getAuthor() {
		return "Graindcafe";
	}
}