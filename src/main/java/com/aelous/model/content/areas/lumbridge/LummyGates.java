package com.aelous.model.content.areas.lumbridge;

import com.aelous.model.entity.player.Player;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.object.ObjectManager;
import com.aelous.model.map.position.Tile;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;

/**
 * @author Patrick van Elderen | March, 27, 2021, 11:18
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class LummyGates extends PacketInteraction {

    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        if(option == 1) {
           if(obj.getId() == 1558 || obj.getId() == 1560) {
               if (obj.tile().equals(3253, 3266) || obj.tile().equals(3253, 3267)) {
                   openEastCowGate();
               } else if (obj.tile().equals(3236, 3296) || obj.tile().equals(3236, 3295)) {
                   openEastChickenGate();
               } else if (obj.tile().equals(3241, 3301) || obj.tile().equals(3241, 3302)) {
                   openPotatoFieldGate();
               } else if (obj.tile().equals(3236, 3284) || obj.tile().equals(3236, 3285)) {
                   openEastChickenGate2();
               } else if (obj.tile().equals(3198, 3282) || obj.tile().equals(3197, 3282)) {
                   openWestCowGate();
               } else if (obj.tile().equals(3181, 3289) || obj.tile().equals(3180, 3289)) {
                   openWestChickenGate();
               } else if (obj.tile().equals(3163, 3290) || obj.tile().equals(3162, 3290)) {
                   openWestWheatGate();
               } else if (obj.tile().equals(3145, 3291) || obj.tile().equals(3146, 3291)) {
                   openWestPotatoGate();
               } else if (obj.tile().equals(3261, 3321) || obj.tile().equals(3262, 3321)) {
                   openNorthEastPotatoGate();
               } else if (obj.tile().equals(3080, 3501) || obj.tile().equals(3079, 3501)) {
                   openEdgevilleGate();
               }
               return true;
           }
            if(obj.getId() == 12986 || obj.getId() == 12987) {
                if (obj.tile().equals(3213, 3261) || obj.tile().equals(3213, 3262)) {
                    openWestSheepGate();
                } else if (obj.tile().equals(3188, 3279) || obj.tile().equals(3189, 3279)) {
                    openWestFarmGate();
                } else if (obj.tile().equals(3186, 3268) || obj.tile().equals(3186, 3269)) {
                    openWestCropGate();
                }
                return true;
            }
            if(obj.getId() == 1559 || obj.getId() == 1567) {
                if (obj.tile().equals(3251, 3266) || obj.tile().equals(3252, 3266)) {
                    closeEastCowGate();
                } else if (obj.tile().equals(3237, 3296) || obj.tile().equals(3238, 3296)) {
                    closeEastChickenGate();
                } else if (obj.tile().equals(3239, 3301) || obj.tile().equals(3240, 3301)) {
                    closePotatoFieldGate();
                } else if (obj.tile().equals(3237, 3285) || obj.tile().equals(3238, 3285)) {
                    closeEastChickenGate2();
                } else if (obj.tile().equals(3198, 3281) || obj.tile().equals(3198, 3280)) {
                    closeWestCowGate();
                } else if (obj.tile().equals(3181, 3288) || obj.tile().equals(3181, 3287)) {
                    closeWestChickenGate();
                } else if (obj.tile().equals(3163, 3289) || obj.tile().equals(3163, 3288)) {
                    closeWestWheatGate();
                } else if (obj.tile().equals(3145, 3292) || obj.tile().equals(3145, 3293)) {
                    closeWestPotatoGate();
                } else if (obj.tile().equals(3261, 3322) || obj.tile().equals(3261, 3323)) {
                    closeNorthEastPotatoGate();
                } else if (obj.tile().equals(3080, 3500) || obj.tile().equals(3080, 3499)) {
                    closeEdgevilleGate();
                }
                return true;
            }
            if(obj.getId() == 12988 || obj.getId() == 12989) {
                if (obj.tile().equals(3212, 3261) || obj.tile().equals(3211, 3261)) {
                    closeWestSheepGate();
                } else if (obj.tile().equals(3188, 3280) || obj.tile().equals(3188, 3281)) {
                    closeWestFarmGate();
                } else if (obj.tile().equals(3185, 3268) || obj.tile().equals(3184, 3268)) {
                    closeWestCropGate();
                }
                return true;
            }
        }
        return false;
    }

    private void openNorthEastPotatoGate() {
        ObjectManager.removeObj(new GameObject(new Tile(3261, 3321),1558, 0, 1));
        ObjectManager.removeObj(new GameObject(new Tile(3262, 3321),1560, 0, 1));
        ObjectManager.addObj(new GameObject(new Tile(3261, 3322),1559, 0, 0));
        ObjectManager.addObj(new GameObject(new Tile(3261, 3323),1567, 0, 0));
    }

    private void closeNorthEastPotatoGate() {
        ObjectManager.addObj(new GameObject(new Tile(3261, 3321), 1558, 0, 1));
        ObjectManager.addObj(new GameObject(new Tile(3262, 3321), 1560, 0, 1));
        ObjectManager.removeObj(new GameObject(new Tile(3261, 3322), 1559, 0, 0));
        ObjectManager.removeObj(new GameObject(new Tile(3261, 3323), 1567, 0, 0));
    }

    private void openWestPotatoGate() {
        ObjectManager.removeObj(new GameObject(new Tile(3145, 3291), 1558, 0, 1));
        ObjectManager.removeObj(new GameObject(new Tile(3146, 3291), 1560, 0, 1));
        ObjectManager.addObj(new GameObject(new Tile(3145, 3292), 1559, 0, 0));
        ObjectManager.addObj(new GameObject(new Tile(3145, 3293), 1567, 0, 0));
    }

    private void closeWestPotatoGate() {
        ObjectManager.addObj(new GameObject(new Tile(3145, 3291), 1558, 0, 1));
        ObjectManager.addObj(new GameObject(new Tile(3146, 3291), 1560, 0, 1));
        ObjectManager.removeObj(new GameObject(new Tile(3145, 3292), 1559, 0, 0));
        ObjectManager.removeObj(new GameObject(new Tile(3145, 3293), 1567, 0, 0));
    }

    private void openWestWheatGate() {
        ObjectManager.removeObj(new GameObject(new Tile(3163, 3290), 1558, 0, 3));
        ObjectManager.removeObj(new GameObject(new Tile(3162, 3290), 1560, 0, 3));
        ObjectManager.addObj(new GameObject(new Tile(3163, 3289), 1559, 0, 2));
        ObjectManager.addObj(new GameObject(new Tile(3163, 3288), 1567, 0, 2));
    }

    private void closeWestWheatGate() {
        ObjectManager.addObj(new GameObject(new Tile(3163, 3290), 1558, 0, 3));
        ObjectManager.addObj(new GameObject(new Tile(3162, 3290), 1560, 0, 3));
        ObjectManager.removeObj(new GameObject(new Tile(3163, 3289), 1559, 0, 2));
        ObjectManager.removeObj(new GameObject(new Tile(3163, 3288), 1567, 0, 2));
    }

    private void openWestCropGate() {
        ObjectManager.removeObj(new GameObject(new Tile(3186, 3268), 12986, 0, 0));
        ObjectManager.removeObj(new GameObject(new Tile(3186, 3269), 12987, 0, 0));
        ObjectManager.addObj(new GameObject(new Tile(3185, 3268), 12988, 0, 3));
        ObjectManager.addObj(new GameObject(new Tile(3184, 3268), 12989, 0, 3));
    }

    private void closeWestCropGate() {
        ObjectManager.addObj(new GameObject(new Tile(3186, 3268), 12986, 0, 0));
        ObjectManager.addObj(new GameObject(new Tile(3186, 3269), 12987, 0, 0));
        ObjectManager.removeObj(new GameObject(new Tile(3185, 3268), 12988, 0, 3));
        ObjectManager.removeObj(new GameObject(new Tile(3184, 3268), 12989, 0, 3));
    }

    private void openWestFarmGate() {
        ObjectManager.removeObj(new GameObject(new Tile(3188, 3279), 12986, 0, 1));
        ObjectManager.removeObj(new GameObject(new Tile(3189, 3279), 12987, 0, 1));
        ObjectManager.addObj(new GameObject(new Tile(3188, 3280), 12988, 0, 0));
        ObjectManager.addObj(new GameObject(new Tile(3188, 3281), 12989, 0, 0));
    }

    private void closeWestFarmGate() {
        ObjectManager.addObj(new GameObject(new Tile(3188, 3279), 12986, 0, 1));
        ObjectManager.addObj(new GameObject(new Tile(3189, 3279), 12987, 0, 1));
        ObjectManager.removeObj(new GameObject(new Tile(3188, 3280), 12988, 0, 0));
        ObjectManager.removeObj(new GameObject(new Tile(3188, 3281), 12989, 0, 0));
    }

    private void openWestChickenGate() {
        ObjectManager.removeObj(new GameObject(new Tile(3181, 3289), 1558, 0, 3));
        ObjectManager.removeObj(new GameObject(new Tile(3180, 3289), 1560, 0, 3));
        ObjectManager.addObj(new GameObject(new Tile(3181, 3288), 1559, 0, 2));
        ObjectManager.addObj(new GameObject(new Tile(3181, 3287), 1567, 0, 2));
    }

    private void closeWestChickenGate() {
        ObjectManager.addObj(new GameObject(new Tile(3181, 3289), 1558, 0, 3));
        ObjectManager.addObj(new GameObject(new Tile(3180, 3289), 1560, 0, 3));
        ObjectManager.removeObj(new GameObject(new Tile(3181, 3288), 1559, 0, 2));
        ObjectManager.removeObj(new GameObject(new Tile(3181, 3287), 1567, 0, 2));
    }

    private void openEastCowGate() {
        ObjectManager.removeObj(new GameObject(new Tile(3253, 3266), 1558, 0, 0));
        ObjectManager.removeObj(new GameObject(new Tile(3253, 3267), 1560, 0, 0));
        ObjectManager.addObj(new GameObject(new Tile(3252, 3266), 1559, 0, 3));
        ObjectManager.addObj(new GameObject(new Tile(3251, 3266), 1567, 0, 3));
    }

    private void closeEastCowGate() {
        ObjectManager.addObj(new GameObject(new Tile(3253, 3266), 1558, 0, 0));
        ObjectManager.addObj(new GameObject(new Tile(3253, 3267), 1560, 0, 0));
        ObjectManager.removeObj(new GameObject(new Tile(3252, 3266), 1559, 0, 3));
        ObjectManager.removeObj(new GameObject(new Tile(3251, 3266), 1567, 0, 3));
    }

    private void openWestCowGate() {
        ObjectManager.removeObj(new GameObject(new Tile(3198, 3282), 1558, 0, 3));
        ObjectManager.removeObj(new GameObject(new Tile(3197, 3282), 1560, 0, 3));
        ObjectManager.addObj(new GameObject(new Tile(3198, 3281), 1559, 0, 2));
        ObjectManager.addObj(new GameObject(new Tile(3198, 3280), 1567, 0, 2));
    }

    private void closeWestCowGate() {
        ObjectManager.addObj(new GameObject(new Tile(3198, 3282), 1558, 0, 3));
        ObjectManager.addObj(new GameObject(new Tile(3197, 3282), 1560, 0, 3));
        ObjectManager.removeObj(new GameObject(new Tile(3198, 3281), 1559, 0, 2));
        ObjectManager.removeObj(new GameObject(new Tile(3198, 3280), 1567, 0, 2));
    }

    private void openEastChickenGate() {
        ObjectManager.removeObj(new GameObject(new Tile(3236, 3295), 1560, 0, 2));
        ObjectManager.removeObj(new GameObject(new Tile(3236, 3296), 1558, 0, 2));
        ObjectManager.addObj(new GameObject(new Tile(3237, 3296), 1559, 0, 1));
        ObjectManager.addObj(new GameObject(new Tile(3238, 3296), 1567, 0, 1));
    }

    private void openEastChickenGate2() {
        ObjectManager.removeObj(new GameObject(new Tile(3236, 3284), 1560, 0, 2));
        ObjectManager.removeObj(new GameObject(new Tile(3236, 3285), 1558, 0, 2));
        ObjectManager.addObj(new GameObject(new Tile(3237, 3285), 1559, 0, 1));
        ObjectManager.addObj(new GameObject(new Tile(3238, 3285), 1567, 0, 1));
    }

    private void openWestSheepGate() {
        ObjectManager.removeObj(new GameObject(new Tile(3213, 3261), 12986, 0, 0));
        ObjectManager.removeObj(new GameObject(new Tile(3213, 3262), 12987, 0, 0));
        ObjectManager.addObj(new GameObject(new Tile(3212, 3261), 12988, 0, 3));
        ObjectManager.addObj(new GameObject(new Tile(3211, 3261), 12989, 0, 3));
    }

    private void closeWestSheepGate() {
        ObjectManager.addObj(new GameObject(new Tile(3213, 3261), 12986, 0, 0));
        ObjectManager.addObj(new GameObject(new Tile(3213, 3262), 12987, 0, 0));
        ObjectManager.removeObj(new GameObject(new Tile(3212, 3261), 12988, 0, 3));
        ObjectManager.removeObj(new GameObject(new Tile(3211, 3261), 12989, 0, 3));
    }

    private void closeEastChickenGate2() {
        ObjectManager.addObj(new GameObject(1560,new Tile(3236, 3284), 0, 2));
        ObjectManager.addObj(new GameObject(1558,new Tile(3236, 3285),0, 2));
        ObjectManager.removeObj(new GameObject(1559,new Tile(3237, 3285),0, 1));
        ObjectManager.removeObj(new GameObject(1567,new Tile(3238, 3285),0, 1));
    }

    private void closeEastChickenGate() {
        ObjectManager.addObj(new GameObject(1560,new Tile(3236, 3295), 0, 2));
        ObjectManager.addObj(new GameObject(1558,new Tile(3236, 3296),0, 2));
        ObjectManager.removeObj(new GameObject(1559,new Tile(3237, 3296), 0, 1));
        ObjectManager.removeObj(new GameObject(1567,new Tile(3238, 3296), 0, 1));
    }

    private void openPotatoFieldGate() {
        ObjectManager.removeObj(new GameObject(1558,new Tile(3241, 3301),0, 0));
        ObjectManager.removeObj(new GameObject(1560,new Tile(3241, 3302), 0, 0));
        ObjectManager.addObj(new GameObject(1567,new Tile(3239, 3301), 0, 3));
        ObjectManager.addObj(new GameObject(1559,new Tile(3240, 3301), 0, 3));
    }

    private void closePotatoFieldGate() {
        ObjectManager.addObj(new GameObject(1558, new Tile(3241, 3301),0, 0));
        ObjectManager.addObj(new GameObject(1560,new Tile(3241, 3302), 0, 0));
        ObjectManager.removeObj(new GameObject(1567,new Tile(3239, 3301), 0, 3));
        ObjectManager.removeObj(new GameObject(1559,new Tile(3240, 3301), 0, 3));
    }

    private void openEdgevilleGate() {
        ObjectManager.removeObj(new GameObject(1558, new Tile(3080, 3501), 0, 3));
        ObjectManager.removeObj(new GameObject(1560, new Tile(3079, 3501), 0, 3));
        ObjectManager.addObj(new GameObject(1559, new Tile(3080, 3500), 0, 2));
        ObjectManager.addObj(new GameObject(1567, new Tile(3080, 3499), 0, 2));
    }

    private void closeEdgevilleGate() {
        ObjectManager.addObj(new GameObject(1558, new Tile(3080, 3501), 0, 3));
        ObjectManager.addObj(new GameObject(1560, new Tile(3079, 3501), 0, 3));
        ObjectManager.removeObj(new GameObject(1559, new Tile(3080, 3500), 0, 2));
        ObjectManager.removeObj(new GameObject(1567, new Tile(3080, 3499), 0, 2));
    }
}
