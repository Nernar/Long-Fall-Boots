let LongFallBoots = WRAP_JAVA(JAVA_MOD);
LongFallBoots.setConfig(__config__);

Callback.addCallback("BlocksDefined", function() {
	Recipes.addShaped2(LongFallBoots.LONG_FALL_BOOTS_ID, 1, 0, [
		"I I",
		"O O",
		LongFallBoots.isHardMode() ? "DND" : "D D"
	], ["I", LongFallBoots.isHardMode() ? 42 : 265, 0,
		"O", 49, 0,
		"D", LongFallBoots.isHardMode() ? 57 : 264, 0,
		"N", 399, 0]);
});
