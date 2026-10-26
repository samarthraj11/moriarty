# Moriarty generated design references

These are the original high-fidelity images generated while defining Moriarty's visual direction.
They are reference material only and intentionally live outside `moriarty-code/app/src/main/res`, so
they are not packaged into the Android application.

## Screen references

1. `01-home-screen.png` — home and level-progress direction
2. `02-all-levels-screen.png` — level browser and difficulty progression
3. `03-puzzle-screen-neutral.png` — unanswered multiple-choice puzzle
4. `04-puzzle-screen-selected.png` — selected-answer variation
5. `05-puzzle-screen-incorrect.png` — incorrect answer, penalty, and hint state
6. `06-solution-screen.png` — solution explanation and result summary
7. `07-daily-challenge-screen.png` — daily challenge overview
8. `08-leaderboard-screen.png` — per-level ranked solving times
9. `09-profile-screen.png` — account, statistics, and achievements
10. `10-settings-screen.png` — accessibility, sound, gameplay, and account settings
11. `11-ui-component-system.png` — palette, controls, states, spacing, and navigation reference

## Artwork references

12. `12-home-hero-art.png` — geometric mirror scene
13. `13-mirrored-clock-art.png` — mirror-clock puzzle art
14. `14-daily-signal-art.png` — alternating-signal challenge art

The production application recreates the main artwork with Jetpack Compose Canvas to keep the
runtime lightweight and density-independent. These images should be used for visual comparison,
future polish, marketing exploration, and design-system decisions—not as bundled game resources.
