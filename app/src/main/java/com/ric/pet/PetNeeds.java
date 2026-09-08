package com.ric.pet;

/** Lightweight virtual-pet needs model. Values are always clamped to 0..100. */
public final class PetNeeds {
    private int hunger = 28;
    private int energy = 78;
    private int mood = 72;
    private int affection = 55;
    private long lastTick = System.currentTimeMillis();

    public void tick() {
        long now = System.currentTimeMillis();
        long minutes = Math.max(0, (now - lastTick) / 60_000L);
        if (minutes == 0) return;
        hunger = clamp(hunger + (int)Math.min(20, minutes / 2 + 1));
        energy = clamp(energy - (int)Math.min(16, minutes / 3 + 1));
        mood = clamp(mood - (int)Math.min(10, minutes / 6));
        affection = clamp(affection - (int)Math.min(8, minutes / 10));
        lastTick = now;
    }

    public void move(boolean fast) { energy = clamp(energy - (fast ? 4 : 2)); hunger = clamp(hunger + 1); }
    public void play() { energy = clamp(energy - 6); hunger = clamp(hunger + 2); mood = clamp(mood + 8); }
    public void eat() { hunger = clamp(hunger - 38); energy = clamp(energy + 4); mood = clamp(mood + 3); }
    public void drink() { mood = clamp(mood + 2); }
    public void sleep() { energy = clamp(energy + 35); hunger = clamp(hunger + 4); mood = clamp(mood + 3); }
    public void pet() { affection = clamp(affection + 12); mood = clamp(mood + 10); }
    public void ignoredAttention() { mood = clamp(mood - 3); }

    public int hunger() { return hunger; }
    public int energy() { return energy; }
    public int mood() { return mood; }
    public int affection() { return affection; }

    public boolean hungry() { return hunger >= 66; }
    public boolean tired() { return energy <= 28; }
    public boolean playful() { return energy >= 55 && mood >= 55 && hunger < 75; }
    public boolean lonely() { return affection <= 28; }

    public String debugSummary() {
        return "Hunger " + hunger + "  Energy " + energy + "  Mood " + mood + "  Love " + affection;
    }

    private int clamp(int v) { return Math.max(0, Math.min(100, v)); }
}
