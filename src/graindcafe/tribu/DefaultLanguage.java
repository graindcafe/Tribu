package graindcafe.tribu;

import java.util.HashMap;

import org.bukkit.ChatColor;

public class DefaultLanguage extends Language
{
	private HashMap<String,String> Strings = new HashMap<String,String>()
			{/**
				 * 
				 */
				private static final long serialVersionUID = 9166935722459443352L;
			{
			put("Sign.Buy","Buy");
			put("Sign.ToggleSpawner","Spawn's switch");
			put("Sign.Spawner","Zombie Spawner");
			put("Sign.HighscoreNames","Top Names");
			put("Sign.HighscorePoints","Top Points");
			put("Message.UnknownItem",ChatColor.YELLOW+"Sorry, unknown item");
			put("Message.ZombieSpawnList",ChatColor.GREEN + "%s");
			put("Message.ConfirmDeletion",ChatColor.YELLOW+"Please confirm the deletion of the %s level by redoing the command");
			put("Message.ThisOperationIsNotCancellable",ChatColor.RED+"This operation is not cancellable!");
			put("Message.LevelUnloaded",ChatColor.GREEN + "Level successfully unloaded");
			put("Message.InvalidVote",ChatColor.RED + "Invalid vote");
			put("Message.ThankyouForYourVote",ChatColor.GREEN + "Thankyou for your vote");
			put("Message.YouCannotVoteAtThisTime",ChatColor.RED + "You cannot vote at this time");
			put("Message.LevelLoadedSuccessfully",ChatColor.GREEN + "Level loaded successfully");
			put("Message.LevelIsAlreadyTheCurrentLevel",ChatColor.RED + "Level %s is already the current level");
			put("Message.UnableToSaveLevel",ChatColor.RED	+ "Unable to save level, try again later");
			put("Message.UnableToLoadLevel",ChatColor.RED	+ "Unable to load level");
			put("Message.NoLevelLoaded","No level loaded, type '/tribu load' to load one,");
			put("Message.NoLevelLoaded2","or '/tribu create' to create a new one,");
			put("Message.TeleportedToDeathSpawn",ChatColor.GREEN + "Teleported to death spawn");
			put("Message.DeathSpawnSet", ChatColor.GREEN+"Death spawn set.");
			put("Message.TeleportedToInitialSpawn",ChatColor.GREEN + "Teleported to initial spawn");
			put("Message.InitialSpawnSet",ChatColor.GREEN + "Initial spawn set.");
			put("Message.UnableToSaveCurrentLevel",ChatColor.RED	+ "Unable to save current level.");
			put("Message.LevelSaveSuccessful",ChatColor.GREEN	+ "Level save successful");
			put("Message.LevelCreated",ChatColor.GREEN + "Level " + ChatColor.LIGHT_PURPLE + "%s" + ChatColor.GREEN + " created");
			put("Message.UnableToDeleteLevel",ChatColor.RED	+ "Unable to delete current level.");
			put("Message.LevelDeleted",ChatColor.GREEN	+ "Level deleted successfully.");
			put("Message.Levels",ChatColor.GREEN + "Levels : %s");
			put("Message.ZombieModeEnabled","Zombie Mode enabled!");
			put("Message.ZombieModeDisabled","Zombie Mode disabled!");
			put("Message.SpawnpointAdded",ChatColor.GREEN + "Spawnpoint added");
			put("Message.SpawnpointRemoved",ChatColor.GREEN + "Spawnpoint removed");
			put("Message.InvalidSpawnName",ChatColor.RED + "Invalid spawn name");
			put("Message.TeleportedToZombieSpawn",ChatColor.GREEN + "Teleported to zombie spawn "+ ChatColor.LIGHT_PURPLE + "%s");
			put("Message.UnableToGiveYouThatItem","Unable to give you that item...");
			put("Message.PurchaseSuccessfulMoney",ChatColor.GREEN + "Purchase successful. Money: "+ ChatColor.DARK_PURPLE + "%s $");
			put("Message.YouDontHaveEnoughMoney",	ChatColor.DARK_RED	+ "You don't have enough money for that!");
			put("Message.MoneyPoints",ChatColor.GREEN + "Money: " + ChatColor.DARK_PURPLE	+ "%s $" + ChatColor.GREEN + " Points: " + ChatColor.RED + "%s");
			put("Message.GameInProgress",ChatColor.YELLOW+ "Game in progress, you will spawn next round");
			put("Message.ZombieHavePrevailed",	ChatColor.GREEN + "Zombies have prevailed!");
			put("Message.YouHaveReachedWave",	ChatColor.GREEN + "You have reached wave "+ ChatColor.LIGHT_PURPLE+ "%s");
			put("Broadcast.MapChosen",ChatColor.GREEN + "Map " + ChatColor.LIGHT_PURPLE +"%s" + ChatColor.GREEN + " has been chosen");
			put("Broadcast.MapVoteStarting",ChatColor.GREEN + "Map vote starting,");
			put("Broadcast.Type",ChatColor.GREEN + "Type ");
			put("Broadcast.SlashVoteForMap",ChatColor.GOLD + "'/tribu vote %s'" + ChatColor.GREEN + " for map "	+ ChatColor.LIGHT_PURPLE + "%s");
			put("Broadcast.VoteClosingInSeconds",ChatColor.GREEN + "Vote closing in " + ChatColor.LIGHT_PURPLE+ "%s" + ChatColor.GREEN + " seconds");
			put("Broadcast.StartingWave",ChatColor.GREEN + "Starting wave " + ChatColor.LIGHT_PURPLE + "%s" + ChatColor.GREEN + ", " + ChatColor.LIGHT_PURPLE + "%s" + ChatColor.GREEN	+ " Zombies @ " + ChatColor.LIGHT_PURPLE + "%s" + ChatColor.GREEN + " health");
			put("Broadcast.Wave",ChatColor.GREEN + "Wave " + ChatColor.LIGHT_PURPLE + "%s" + ChatColor.GREEN + " starting in "+ ChatColor.LIGHT_PURPLE + "%s" + ChatColor.GREEN + " seconds.");
			put("Broadcast.WaveComplete",ChatColor.GREEN + "Wave Complete");
			put("Info.LevelFound","%s levels found");
			put("Info.Enable","Starting Tribu by Graindcafe, original author : samp20");
			put("Info.Disable","Stopping Tribu");
			put("Info.LevelSaved","Level saved");
			put("Info.LevelFolderDoesntExist","Level folder doesn't exist");
			put("Warning.AllSpawnsCurrentlyUnloaded","All zombies spawns are currently unloaded.");
			put("Warning.UnableToSaveLevel","Unable to save level");
			put("Warning.ThisCommandCannotBeUsedFromTheConsole","This command cannot be used from the console");
			put("Warning.IOErrorOnFileDelete","IO error on file delete");
			put("Warning.LanguageFileOutdated","Your current language file is outdated");
			put("Warning.UnableToAddSign","Unable to add sign, maybe you've changed your locales, or signs' tags.");
			put("Severe.WorldInvalidFileVersion","World invalid file version");
			put("Severe.WorldDoesntExist","World doesn't exist");
			put("Severe.ErrorDuringLevelLoading","Error during level loading : %s");
			put("Severe.ErrorDuringLevelSaving","Error during level saving : %s");
			put("Severe.Exception","Exception: %s");
			}};
			
	@Override
	public String get(String key){
		return Strings.get(key);
	}

	@Override
	public byte getVersion()
	{
		return Constants.LanguageFileVersion;
	}
}