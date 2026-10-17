HUMAN JOURNEY v0.3.3 — ERA 0 RELEASE CANDIDATE
Minecraft Java 1.21.8 / Forge

MAIN CHANGE FROM v0.3.2
Survival water collection is fixed by checking:
- weapon.mainhand
- weapon.offhand
- hotbar.*
- inventory.*

The previous build only watched inventory.*, while a freshly-filled survival
Water Bottle normally remains in the selected hotbar/main-hand slot.

Expected water loop:
1. STUDENT fills a glass bottle from sea/river.
2. Vanilla Water Bottle becomes Tough As Nails Dirty Water Bottle.
3. Dirty Water can be purified in any of three ways:
   - campfire cooking
   - furnace boiling
   - crafting with charcoal
4. Purified Water triggers Water for Life when the Survival branch is ready.

ERA TREE LAYOUT
No gameplay milestones were rearranged in v0.3.3.
Minecraft's vanilla advancement UI calculates node positions automatically and
each advancement may have only one parent. It cannot draw a true three-branch merge.

For now WE CAN STAY remains attached visually to the middle Journey branch,
while its real logic requires ALL three branch endpoints:
- Rest at Last
- Carry What You Can
- A New Continent

This preserves the clearest visual approximation without changing progression
merely to manipulate the GUI.

UPDATING THE PACK LATER
Adding new advancement files does NOT require the class to restart.
Human Journey stores shared civilization progress in the hj_civ scoreboard and
visible progress is synchronized from it.

For safe future updates:
- keep existing advancement resource IDs stable
- keep existing hj_civ score names stable
- freely change titles, descriptions, icons, backgrounds and most parent layout
- adding new future-era advancements is safe
- do not rename/delete a completed milestone without adding a migration
- if new mandatory Era-0 milestones are added AFTER WE CAN STAY has already been
  completed, a migration is needed because the civilization has already passed Era 0

LIVE-SERVER MIGRATION
The class has already completed some Era-0 actions before the datapack existed.
Do not make pupils repeat them.

v0.3.3 adds administrator helpers, for example:
  /function human_journey:admin/mark/first_flame
  /function human_journey:admin/mark/first_wood
  /function human_journey:admin/mark/place_to_work
  /function human_journey:admin/mark/beyond_camp

Run only the milestones the class genuinely completed, then Human Journey will sync
the visible tree for students and teacher.

BEFORE LIVE CLASS
1. Test water once more in Survival.
2. /reload must show no pack errors.
3. Put the pack on a BACKUP COPY of the Earth server.
4. Assign roles once:
   execute as <pupil> run function human_journey:setup/student
   execute as <teacher> run function human_journey:setup/teacher
5. Mark already-completed class milestones with admin/mark functions.
6. Check the advancement screen and locked recipes.
7. Make a fresh backup of the real world, then install the same pack live.

Player role tags persist in that world between logins.
