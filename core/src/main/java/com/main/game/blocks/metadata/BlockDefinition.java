package com.main.game.blocks.metadata;

public final class BlockDefinition {

    public enum PaletteFallback {
        NONE,
        GRASS,
        DIRT,
        STONE,
        BEDROCK,
        SAND,
        WOOD,
        LEAVES,
        PLANKS,
        SNOW,
        ICE,
        SANDSTONE,
        CACTUS
    }

    private final String id;
    private final String textureName;
    private final PaletteFallback paletteFallback;
    private final boolean preferPalette;
    private final float hardness;
    private final boolean solid;
    private final boolean breakable;
    private final boolean placeable;
    private final String dropItemId;
    private final int requiredPickaxeLevel;
    private final boolean ore;

    private BlockDefinition(Builder builder) {
        this.id = builder.id;
        this.textureName = builder.textureName;
        this.paletteFallback = builder.paletteFallback;
        this.preferPalette = builder.preferPalette;
        this.hardness = builder.hardness;
        this.solid = builder.solid;
        this.breakable = builder.breakable;
        this.placeable = builder.placeable;
        this.dropItemId = builder.dropItemId;
        this.requiredPickaxeLevel = builder.requiredPickaxeLevel;
        this.ore = builder.ore;
    }

    public static Builder builder(String id) {
        return new Builder(id);
    }

    public String getId() {
        return id;
    }

    public String getTextureName() {
        return textureName;
    }

    public PaletteFallback getPaletteFallback() {
        return paletteFallback;
    }

    public boolean shouldPreferPalette() {
        return preferPalette;
    }

    public float getHardness() {
        return hardness;
    }

    public boolean isSolid() {
        return solid;
    }

    public boolean isBreakable() {
        return breakable;
    }

    public boolean isPlaceable() {
        return placeable;
    }

    public String getDropItemId() {
        return dropItemId == null ? id : dropItemId;
    }

    public int getRequiredPickaxeLevel() {
        return requiredPickaxeLevel;
    }

    public boolean isOre() {
        return ore;
    }

    public static final class Builder {
        private final String id;
        private String textureName;
        private PaletteFallback paletteFallback = PaletteFallback.NONE;
        private boolean preferPalette;
        private float hardness = 0.6f;
        private boolean solid = true;
        private boolean breakable = true;
        private boolean placeable;
        private String dropItemId;
        private int requiredPickaxeLevel;
        private boolean ore;

        private Builder(String id) {
            this.id = id;
            this.textureName = id;
        }

        public Builder textureName(String textureName) {
            this.textureName = textureName;
            return this;
        }

        public Builder paletteFallback(PaletteFallback paletteFallback) {
            this.paletteFallback = paletteFallback == null ? PaletteFallback.NONE : paletteFallback;
            return this;
        }

        public Builder preferPalette() {
            this.preferPalette = true;
            return this;
        }

        public Builder hardness(float hardness) {
            this.hardness = hardness;
            return this;
        }

        public Builder solid(boolean solid) {
            this.solid = solid;
            return this;
        }

        public Builder breakable(boolean breakable) {
            this.breakable = breakable;
            return this;
        }

        public Builder placeable() {
            this.placeable = true;
            return this;
        }

        public Builder placeableIf(boolean placeable) {
            this.placeable = placeable;
            return this;
        }

        public Builder dropItemId(String dropItemId) {
            this.dropItemId = dropItemId;
            return this;
        }

        public Builder requiredPickaxeLevel(int requiredPickaxeLevel) {
            this.requiredPickaxeLevel = requiredPickaxeLevel;
            return this;
        }

        public Builder ore() {
            this.ore = true;
            return this;
        }

        public BlockDefinition build() {
            return new BlockDefinition(this);
        }
    }
}
