package com.hypixelcompare;

import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SkyblockItemStats {
    public final Map<String, Integer> stats = new HashMap<>();
    private final String itemName;
    private String rarity;
    private final String itemType;
    
    // Patterns for text with color codes (if available)
    private static final Pattern STAT_PATTERN = Pattern.compile("§7([^:]+): §[a-f0-9]\\+?(\\d+)");
    private static final Pattern RARITY_PATTERN = Pattern.compile("§[a-f0-9]§l([A-Z ]+)");
    private static final Pattern DAMAGE_PATTERN = Pattern.compile("§7Damage: §c\\+?(\\d+)");
    private static final Pattern DEFENSE_PATTERN = Pattern.compile("§7Defense: §a\\+?(\\d+)");
    private static final Pattern STRENGTH_PATTERN = Pattern.compile("§7Strength: §c\\+?(\\d+)");
    private static final Pattern CRIT_CHANCE_PATTERN = Pattern.compile("§7Crit Chance: §c\\+?(\\d+)%");
    private static final Pattern CRIT_DAMAGE_PATTERN = Pattern.compile("§7Crit Damage: §c\\+?(\\d+)%");
    private static final Pattern SPEED_PATTERN = Pattern.compile("§7Speed: §a\\+?(\\d+)");
    private static final Pattern HEALTH_PATTERN = Pattern.compile("§7Health: §a\\+?(\\d+)");
    private static final Pattern MANA_PATTERN = Pattern.compile("§7Mana: §b\\+?(\\d+)");
    
    // Patterns for plain text (without color codes)
    private static final Pattern DAMAGE_PLAIN_PATTERN = Pattern.compile("Damage: \\+?(\\d+)");
    private static final Pattern DEFENSE_PLAIN_PATTERN = Pattern.compile("Defense: \\+?(\\d+)");
    private static final Pattern STRENGTH_PLAIN_PATTERN = Pattern.compile("Strength: \\+?(\\d+)");
    private static final Pattern CRIT_CHANCE_PLAIN_PATTERN = Pattern.compile("Crit Chance: \\+?(\\d+)%");
    private static final Pattern CRIT_DAMAGE_PLAIN_PATTERN = Pattern.compile("Crit Damage: \\+?(\\d+)%");
    private static final Pattern SPEED_PLAIN_PATTERN = Pattern.compile("Speed: \\+?(\\d+)");
    private static final Pattern HEALTH_PLAIN_PATTERN = Pattern.compile("Health: \\+?(\\d+)");
    private static final Pattern MANA_PLAIN_PATTERN = Pattern.compile("Mana: \\+?(\\d+)");
    private static final Pattern RARITY_PLAIN_PATTERN = Pattern.compile("([A-Z]+) [A-Z]+$"); // UNCOMMON PICKAXE, RARE SWORD, etc.
    
    // Mining and specialized stats
    private static final Pattern MINING_SPEED_PLAIN_PATTERN = Pattern.compile("Mining Speed: \\+?(\\d+)");
    private static final Pattern MINING_FORTUNE_PLAIN_PATTERN = Pattern.compile("Mining Fortune: \\+?(\\d+)");
    private static final Pattern BREAKING_POWER_PLAIN_PATTERN = Pattern.compile("Breaking Power (\\d+)");
    private static final Pattern INTELLIGENCE_PLAIN_PATTERN = Pattern.compile("Intelligence: \\+?(\\d+)");
    private static final Pattern MAGIC_FIND_PLAIN_PATTERN = Pattern.compile("Magic Find: \\+?(\\d+)");
    private static final Pattern PET_LUCK_PLAIN_PATTERN = Pattern.compile("Pet Luck: \\+?(\\d+)");
    private static final Pattern SEA_CREATURE_CHANCE_PLAIN_PATTERN = Pattern.compile("Sea Creature Chance: \\+?(\\d+)%");
    private static final Pattern FISHING_SPEED_PLAIN_PATTERN = Pattern.compile("Fishing Speed: \\+?(\\d+)");
    private static final Pattern FEROCITY_PLAIN_PATTERN = Pattern.compile("Ferocity: \\+?(\\d+)");
    private static final Pattern ABILITY_DAMAGE_PLAIN_PATTERN = Pattern.compile("Ability Damage: \\+?(\\d+)%");
    private static final Pattern BONUS_ATTACK_SPEED_PLAIN_PATTERN = Pattern.compile("Bonus Attack Speed: \\+?(\\d+)%");
    private static final Pattern TRUE_DEFENSE_PLAIN_PATTERN = Pattern.compile("True Defense: \\+?(\\d+)");
    private static final Pattern VITALITY_PLAIN_PATTERN = Pattern.compile("Vitality: \\+?(\\d+)");
    private static final Pattern FARMING_FORTUNE_PLAIN_PATTERN = Pattern.compile("Farming Fortune: \\+?(\\d+)");
    private static final Pattern FORAGING_FORTUNE_PLAIN_PATTERN = Pattern.compile("Foraging Fortune: \\+?(\\d+)");
    
    public SkyblockItemStats(String itemName, String rarity, String itemType) {
        this.itemName = itemName;
        this.rarity = rarity;
        this.itemType = itemType;
    }
    
    public static SkyblockItemStats parseItem(ItemStack item) {
        String itemName = item.getName().getString();
        String rarity = "COMMON";
        String itemType = "UNKNOWN";
        
        HypixelCompare.LOGGER.info("=== PARSING ITEM: " + itemName + " ===");
        
        SkyblockItemStats stats = new SkyblockItemStats(itemName, rarity, itemType);
        
        List<Text> lore = item.getTooltip(net.minecraft.item.Item.TooltipContext.DEFAULT, null, net.minecraft.item.tooltip.TooltipType.BASIC);
        
        HypixelCompare.LOGGER.info("Total lore lines: " + lore.size());
        
        for (int i = 0; i < lore.size(); i++) {
            Text line = lore.get(i);
            String lineText = line.getString();
            
            HypixelCompare.LOGGER.info("Line " + i + ": '" + lineText + "'");
            
            // Try to parse rarity from both patterns
            Matcher rarityMatcher = RARITY_PATTERN.matcher(lineText);
            if (rarityMatcher.find()) {
                rarity = rarityMatcher.group(1);
                HypixelCompare.LOGGER.info("Found rarity (colored): " + rarity);
            } else {
                Matcher rarityPlainMatcher = RARITY_PLAIN_PATTERN.matcher(lineText);
                if (rarityPlainMatcher.find()) {
                    rarity = rarityPlainMatcher.group(1);
                    HypixelCompare.LOGGER.info("Found rarity (plain): " + rarity);
                }
            }
            
            // Try colored patterns first, then plain patterns
            stats.parseStat(lineText, "Damage", DAMAGE_PATTERN);
            stats.parseStat(lineText, "Damage", DAMAGE_PLAIN_PATTERN);
            stats.parseStat(lineText, "Defense", DEFENSE_PATTERN);
            stats.parseStat(lineText, "Defense", DEFENSE_PLAIN_PATTERN);
            stats.parseStat(lineText, "Strength", STRENGTH_PATTERN);
            stats.parseStat(lineText, "Strength", STRENGTH_PLAIN_PATTERN);
            stats.parseStat(lineText, "Crit Chance", CRIT_CHANCE_PATTERN);
            stats.parseStat(lineText, "Crit Chance", CRIT_CHANCE_PLAIN_PATTERN);
            stats.parseStat(lineText, "Crit Damage", CRIT_DAMAGE_PATTERN);
            stats.parseStat(lineText, "Crit Damage", CRIT_DAMAGE_PLAIN_PATTERN);
            stats.parseStat(lineText, "Speed", SPEED_PATTERN);
            stats.parseStat(lineText, "Speed", SPEED_PLAIN_PATTERN);
            stats.parseStat(lineText, "Health", HEALTH_PATTERN);
            stats.parseStat(lineText, "Health", HEALTH_PLAIN_PATTERN);
            stats.parseStat(lineText, "Mana", MANA_PATTERN);
            stats.parseStat(lineText, "Mana", MANA_PLAIN_PATTERN);
            
            // Mining and specialized stats
            stats.parseStat(lineText, "Mining Speed", MINING_SPEED_PLAIN_PATTERN);
            stats.parseStat(lineText, "Mining Fortune", MINING_FORTUNE_PLAIN_PATTERN);
            stats.parseStat(lineText, "Breaking Power", BREAKING_POWER_PLAIN_PATTERN);
            stats.parseStat(lineText, "Intelligence", INTELLIGENCE_PLAIN_PATTERN);
            stats.parseStat(lineText, "Magic Find", MAGIC_FIND_PLAIN_PATTERN);
            stats.parseStat(lineText, "Pet Luck", PET_LUCK_PLAIN_PATTERN);
            stats.parseStat(lineText, "Sea Creature Chance", SEA_CREATURE_CHANCE_PLAIN_PATTERN);
            stats.parseStat(lineText, "Fishing Speed", FISHING_SPEED_PLAIN_PATTERN);
            stats.parseStat(lineText, "Ferocity", FEROCITY_PLAIN_PATTERN);
            stats.parseStat(lineText, "Ability Damage", ABILITY_DAMAGE_PLAIN_PATTERN);
            stats.parseStat(lineText, "Bonus Attack Speed", BONUS_ATTACK_SPEED_PLAIN_PATTERN);
            stats.parseStat(lineText, "True Defense", TRUE_DEFENSE_PLAIN_PATTERN);
            stats.parseStat(lineText, "Vitality", VITALITY_PLAIN_PATTERN);
            stats.parseStat(lineText, "Farming Fortune", FARMING_FORTUNE_PLAIN_PATTERN);
            stats.parseStat(lineText, "Foraging Fortune", FORAGING_FORTUNE_PLAIN_PATTERN);
            
            Matcher generalMatcher = STAT_PATTERN.matcher(lineText);
            if (generalMatcher.find()) {
                String statName = generalMatcher.group(1);
                int value = Integer.parseInt(generalMatcher.group(2));
                stats.stats.put(statName, value);
                HypixelCompare.LOGGER.info("General stat found: " + statName + " = " + value);
            }
        }
        
        HypixelCompare.LOGGER.info("Final parsed stats: " + stats.stats);
        HypixelCompare.LOGGER.info("=== END PARSING ===");
        
        // Update the rarity in the stats object
        stats.rarity = rarity;
        
        return stats;
    }
    
    private void parseStat(String line, String statName, Pattern pattern) {
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            int value = Integer.parseInt(matcher.group(1));
            stats.put(statName, value);
            HypixelCompare.LOGGER.info("Specific stat found: " + statName + " = " + value + " (pattern matched)");
        }
    }
    
    public List<String> getFormattedStats() {
        List<String> formatted = new ArrayList<>();
        
        formatted.add("§6" + itemName);
        formatted.add("§7Rarity: §" + getRarityColor() + rarity);
        formatted.add("");
        
        String[] statOrder = {"Damage", "Defense", "Strength", "Crit Chance", "Crit Damage", 
                            "Speed", "Health", "Mana"};
        
        for (String stat : statOrder) {
            if (stats.containsKey(stat)) {
                String color = getStatColor(stat);
                String suffix = (stat.equals("Crit Chance") || stat.equals("Crit Damage")) ? "%" : "";
                formatted.add("§7" + stat + ": " + color + "+" + stats.get(stat) + suffix);
            }
        }
        
        for (Map.Entry<String, Integer> entry : stats.entrySet()) {
            if (!Arrays.asList(statOrder).contains(entry.getKey())) {
                formatted.add("§7" + entry.getKey() + ": §a+" + entry.getValue());
            }
        }
        
        return formatted;
    }
    
    private String getRarityColor() {
        switch (rarity.toUpperCase()) {
            case "COMMON": return "f";
            case "UNCOMMON": return "a";
            case "RARE": return "9";
            case "EPIC": return "5";
            case "LEGENDARY": return "6";
            case "MYTHIC": return "d";
            case "DIVINE": return "b";
            case "SPECIAL": return "c";
            default: return "f";
        }
    }
    
    private String getStatColor(String stat) {
        switch (stat) {
            case "Damage":
            case "Strength":
            case "Crit Chance":
            case "Crit Damage":
                return "§c";
            case "Defense":
            case "Health":
            case "Speed":
                return "§a";
            case "Mana":
                return "§b";
            default:
                return "§a";
        }
    }
    
    public int getStat(String statName) {
        return stats.getOrDefault(statName, 0);
    }
    
    public String getItemName() {
        return itemName;
    }
    
    public String getRarity() {
        return rarity;
    }
}