package com.aelous.model.map.route.collisionmaps;

public final class CollisionMap {

    private static final int BLOCKED_TILE = 0x200000;
    //private static final int OBJECT_TILE = 0x100;

    private final int inset_x;
    private final int inset_y;
    private final int width;
    private final int height;
    public final int[][] adjacencies;

    public CollisionMap() {
        inset_x = 0;
        inset_y = 0;
        width = 104;
        height = 104;
        adjacencies = new int[width][height];
        init();
    }

    public void init() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++)
                if (x == 0 || y == 0 || x == width - 1
                    || y == height - 1)
                    adjacencies[x][y] = 0xffffff;
                else
                    adjacencies[x][y] = 0x1000000;

        }
    }

    public void mark_wall(int y, int orientation, int x, int group, boolean walkable) {
        x -= inset_x;
        y -= inset_y;
        if (group == 0) {
            if (orientation == 0) {
                set(x, y, 128);
                set(x - 1, y, 8);
            }
            if (orientation == 1) {
                set(x, y, 2);
                set(x, y + 1, 32);
            }
            if (orientation == 2) {
                set(x, y, 8);
                set(x + 1, y, 128);
            }
            if (orientation == 3) {
                set(x, y, 32);
                set(x, y - 1, 2);
            }
        }
        if (group == 1 || group == 3) {
            if (orientation == 0) {
                set(x, y, 1);
                set(x - 1, y + 1, 16);
            }
            if (orientation == 1) {
                set(x, y, 4);
                set(x + 1, y + 1, 64);
            }
            if (orientation == 2) {
                set(x, y, 16);
                set(x + 1, y - 1, 1);
            }
            if (orientation == 3) {
                set(x, y, 64);
                set(x - 1, y - 1, 4);
            }
        }
        if (group == 2) {
            if (orientation == 0) {
                set(x, y, 130);
                set(x - 1, y, 8);
                set(x, y + 1, 32);
            }
            if (orientation == 1) {
                set(x, y, 10);
                set(x, y + 1, 32);
                set(x + 1, y, 128);
            }
            if (orientation == 2) {
                set(x, y, 40);
                set(x + 1, y, 128);
                set(x, y - 1, 2);
            }
            if (orientation == 3) {
                set(x, y, 160);
                set(x, y - 1, 2);
                set(x - 1, y, 8);
            }
        }
        if (walkable) {
            if (group == 0) {
                if (orientation == 0) {
                    set(x, y, 0x10000);
                    set(x - 1, y, 4096);
                }
                if (orientation == 1) {
                    set(x, y, 1024);
                    set(x, y + 1, 16384);
                }
                if (orientation == 2) {
                    set(x, y, 4096);
                    set(x + 1, y, 0x10000);
                }
                if (orientation == 3) {
                    set(x, y, 16384);
                    set(x, y - 1, 1024);
                }
            }
            if (group == 1 || group == 3) {
                if (orientation == 0) {
                    set(x, y, 512);
                    set(x - 1, y + 1, 8192);
                }
                if (orientation == 1) {
                    set(x, y, 2048);
                    set(x + 1, y + 1, 32768);
                }
                if (orientation == 2) {
                    set(x, y, 8192);
                    set(x + 1, y - 1, 512);
                }
                if (orientation == 3) {
                    set(x, y, 32768);
                    set(x - 1, y - 1, 2048);
                }
            }
            if (group == 2) {
                if (orientation == 0) {
                    set(x, y, 0x10400);
                    set(x - 1, y, 4096);
                    set(x, y + 1, 16384);
                }
                if (orientation == 1) {
                    set(x, y, 5120);
                    set(x, y + 1, 16384);
                    set(x + 1, y, 0x10000);
                }
                if (orientation == 2) {
                    set(x, y, 20480);
                    set(x + 1, y, 0x10000);
                    set(x, y - 1, 1024);
                }
                if (orientation == 3) {
                    set(x, y, 0x14000);
                    set(x, y - 1, 1024);
                    set(x - 1, y, 4096);
                }
            }
        }
    }

    public void mark_interactive_obj(boolean walkable, int width, int height, int x, int y, int orientation) {
        int flag = 256;
        if (walkable)
            flag += 0x20000;

        x -= inset_x;
        y -= inset_y;
        if (orientation == 1 || orientation == 3) {
            int pos = width;
            width = height;
            height = pos;
        }
        for (int x_pos = x; x_pos < x + width; x_pos++)
            if (x_pos >= 0 && x_pos < this.width) {
                for (int y_pos = y; y_pos < y + height; y_pos++)
                    if (y_pos >= 0 && y_pos < this.height)
                        set(x_pos, y_pos, flag);

            }

    }

    public void set_solid_flag(int x, int y) {
        x -= inset_x;
        y -= inset_y;
        adjacencies[x][y] |= BLOCKED_TILE;
    }

    private void set(int x, int y, int flag) {
        adjacencies[x][y] |= flag;
    }

    public void clear_wall(int orientation, int type, boolean walkable, int x, int y) {
        x -= inset_x;
        y -= inset_y;
        if (type == 0) {
            if (orientation == 0) {
                unset(128, x, y);
                unset(8, x - 1, y);
            }
            if (orientation == 1) {
                unset(2, x, y);
                unset(32, x, y + 1);
            }
            if (orientation == 2) {
                unset(8, x, y);
                unset(128, x + 1, y);
            }
            if (orientation == 3) {
                unset(32, x, y);
                unset(2, x, y - 1);
            }
        }
        if (type == 1 || type == 3) {
            if (orientation == 0) {
                unset(1, x, y);
                unset(16, x - 1, y + 1);
            }
            if (orientation == 1) {
                unset(4, x, y);
                unset(64, x + 1, y + 1);
            }
            if (orientation == 2) {
                unset(16, x, y);
                unset(1, x + 1, y - 1);
            }
            if (orientation == 3) {
                unset(64, x, y);
                unset(4, x - 1, y - 1);
            }
        }
        if (type == 2) {
            if (orientation == 0) {
                unset(130, x, y);
                unset(8, x - 1, y);
                unset(32, x, y + 1);
            }
            if (orientation == 1) {
                unset(10, x, y);
                unset(32, x, y + 1);
                unset(128, x + 1, y);
            }
            if (orientation == 2) {
                unset(40, x, y);
                unset(128, x + 1, y);
                unset(2, x, y - 1);
            }
            if (orientation == 3) {
                unset(160, x, y);
                unset(2, x, y - 1);
                unset(8, x - 1, y);
            }
        }
        if (walkable) {
            if (type == 0) {
                if (orientation == 0) {
                    unset(0x10000, x, y);
                    unset(4096, x - 1, y);
                }
                if (orientation == 1) {
                    unset(1024, x, y);
                    unset(16384, x, y + 1);
                }
                if (orientation == 2) {
                    unset(4096, x, y);
                    unset(0x10000, x + 1, y);
                }
                if (orientation == 3) {
                    unset(16384, x, y);
                    unset(1024, x, y - 1);
                }
            }
            if (type == 1 || type == 3) {
                if (orientation == 0) {
                    unset(512, x, y);
                    unset(8192, x - 1, y + 1);
                }
                if (orientation == 1) {
                    unset(2048, x, y);
                    unset(32768, x + 1, y + 1);
                }
                if (orientation == 2) {
                    unset(8192, x, y);
                    unset(512, x + 1, y - 1);
                }
                if (orientation == 3) {
                    unset(32768, x, y);
                    unset(2048, x - 1, y - 1);
                }
            }
            if (type == 2) {
                if (orientation == 0) {
                    unset(0x10400, x, y);
                    unset(4096, x - 1, y);
                    unset(16384, x, y + 1);
                }
                if (orientation == 1) {
                    unset(5120, x, y);
                    unset(16384, x, y + 1);
                    unset(0x10000, x + 1, y);
                }
                if (orientation == 2) {
                    unset(20480, x, y);
                    unset(0x10000, x + 1, y);
                    unset(1024, x, y - 1);
                }
                if (orientation == 3) {
                    unset(0x14000, x, y);
                    unset(1024, x, y - 1);
                    unset(4096, x - 1, y);
                }
            }
        }
    }

    public void clear_interactive_obj(int orientation, int width, int x, int y, int height, boolean walkable) {
        int flag = 256;
        if (walkable)
            flag += 0x20000;

        x -= inset_x;
        y -= inset_y;
        if (orientation == 1 || orientation == 3) {
            int pos = width;
            width = height;
            height = pos;
        }
        for (int x_pos = x; x_pos < x + width; x_pos++)
            if (x_pos >= 0 && x_pos < this.width) {
                for (int y_pos = y; y_pos < y + height; y_pos++)
                    if (y_pos >= 0 && y_pos < this.height)
                        unset(flag, x_pos, y_pos);

            }

    }

    private void unset(int flag, int x, int k) {
        adjacencies[x][k] &= 0xffffff - flag;
    }

    public void clear_ground_decor(int y, int x) {
        x -= inset_x;
        y -= inset_y;
        adjacencies[x][y] &= 0xdfffff;
    }

    public boolean obstruction_wall(int travel_x, int x, int y, int obstruction_orientation, int obstruction_type, int travel_y) {
        if (x == travel_x && y == travel_y)
            return true;

        x -= inset_x;
        y -= inset_y;
        travel_x -= inset_x;
        travel_y -= inset_y;
        if (obstruction_type == 0)
            if (obstruction_orientation == 0) {
                if (x == travel_x - 1 && y == travel_y)
                    return true;
                if (x == travel_x && y == travel_y + 1
                    && (adjacencies[x][y] & 0x1280120) == 0)
                    return true;
                if (x == travel_x && y == travel_y - 1
                    && (adjacencies[x][y] & 0x1280102) == 0)
                    return true;
            } else if (obstruction_orientation == 1) {
                if (x == travel_x && y == travel_y + 1)
                    return true;
                if (x == travel_x - 1 && y == travel_y
                    && (adjacencies[x][y] & 0x1280108) == 0)
                    return true;
                if (x == travel_x + 1 && y == travel_y
                    && (adjacencies[x][y] & 0x1280180) == 0)
                    return true;
            } else if (obstruction_orientation == 2) {
                if (x == travel_x + 1 && y == travel_y)
                    return true;
                if (x == travel_x && y == travel_y + 1
                    && (adjacencies[x][y] & 0x1280120) == 0)
                    return true;
                if (x == travel_x && y == travel_y - 1
                    && (adjacencies[x][y] & 0x1280102) == 0)
                    return true;
            } else if (obstruction_orientation == 3) {
                if (x == travel_x && y == travel_y - 1)
                    return true;
                if (x == travel_x - 1 && y == travel_y
                    && (adjacencies[x][y] & 0x1280108) == 0)
                    return true;
                if (x == travel_x + 1 && y == travel_y
                    && (adjacencies[x][y] & 0x1280180) == 0)
                    return true;
            }

        if (obstruction_type == 2)
            if (obstruction_orientation == 0) {
                if (x == travel_x - 1 && y == travel_y)
                    return true;

                if (x == travel_x && y == travel_y + 1)
                    return true;

                if (x == travel_x + 1 && y == travel_y
                    && (adjacencies[x][y] & 0x1280180) == 0)
                    return true;

                if (x == travel_x && y == travel_y - 1
                    && (adjacencies[x][y] & 0x1280102) == 0)
                    return true;

            } else if (obstruction_orientation == 1) {
                if (x == travel_x - 1 && y == travel_y
                    && (adjacencies[x][y] & 0x1280108) == 0)
                    return true;

                if (x == travel_x && y == travel_y + 1)
                    return true;

                if (x == travel_x + 1 && y == travel_y)
                    return true;

                if (x == travel_x && y == travel_y - 1
                    && (adjacencies[x][y] & 0x1280102) == 0)
                    return true;

            } else if (obstruction_orientation == 2) {
                if (x == travel_x - 1 && y == travel_y
                    && (adjacencies[x][y] & 0x1280108) == 0)
                    return true;

                if (x == travel_x && y == travel_y + 1
                    && (adjacencies[x][y] & 0x1280120) == 0)
                    return true;

                if (x == travel_x + 1 && y == travel_y)
                    return true;

                if (x == travel_x && y == travel_y - 1)
                    return true;

            } else if (obstruction_orientation == 3) {
                if (x == travel_x - 1 && y == travel_y)
                    return true;

                if (x == travel_x && y == travel_y + 1
                    && (adjacencies[x][y] & 0x1280120) == 0)
                    return true;

                if (x == travel_x + 1 && y == travel_y
                    && (adjacencies[x][y] & 0x1280180) == 0)
                    return true;

                if (x == travel_x && y == travel_y - 1)
                    return true;

            }

        if (obstruction_type == 9) {
            if (x == travel_x && y == travel_y + 1 && (adjacencies[x][y] & 0x20) == 0)
                return true;

            if (x == travel_x && y == travel_y - 1 && (adjacencies[x][y] & 2) == 0)
                return true;

            if (x == travel_x - 1 && y == travel_y && (adjacencies[x][y] & 8) == 0)
                return true;

            if (x == travel_x + 1 && y == travel_y && (adjacencies[x][y] & 0x80) == 0)
                return true;
        }
        return false;
    }

    public boolean obstruction_decor(int travel_x, int travel_y, int y, int obstruction_type, int obstruction_orientation, int x) {
        if (x == travel_x && y == travel_y)
            return true;

        x -= inset_x;
        y -= inset_y;
        travel_x -= inset_x;
        travel_y -= inset_y;
        if (obstruction_type == 6 || obstruction_type == 7) {
            if (obstruction_type == 7)
                obstruction_orientation = obstruction_orientation + 2 & 3;
            if (obstruction_orientation == 0) {
                if (x == travel_x + 1 && y == travel_y && (adjacencies[x][y] & 0x80) == 0)
                    return true;

                if (x == travel_x && y == travel_y - 1 && (adjacencies[x][y] & 2) == 0)
                    return true;

            } else if (obstruction_orientation == 1) {
                if (x == travel_x - 1 && y == travel_y && (adjacencies[x][y] & 8) == 0)
                    return true;

                if (x == travel_x && y == travel_y - 1 && (adjacencies[x][y] & 2) == 0)
                    return true;

            } else if (obstruction_orientation == 2) {
                if (x == travel_x - 1 && y == travel_y && (adjacencies[x][y] & 8) == 0)
                    return true;

                if (x == travel_x && y == travel_y + 1 && (adjacencies[x][y] & 0x20) == 0)
                    return true;

            } else if (obstruction_orientation == 3) {
                if (x == travel_x + 1 && y == travel_y && (adjacencies[x][y] & 0x80) == 0)
                    return true;

                if (x == travel_x && y == travel_y + 1 && (adjacencies[x][y] & 0x20) == 0)
                    return true;

            }
        }
        if (obstruction_type == 8) {
            if (x == travel_x && y == travel_y + 1 && (adjacencies[x][y] & 0x20) == 0)
                return true;

            if (x == travel_x && y == travel_y - 1 && (adjacencies[x][y] & 2) == 0)
                return true;

            if (x == travel_x - 1 && y == travel_y && (adjacencies[x][y] & 8) == 0)
                return true;

            if (x == travel_x + 1 && y == travel_y && (adjacencies[x][y] & 0x80) == 0)
                return true;

        }
        return false;
    }

    public boolean obstruction(int travel_y, int travel_x, int x, int obstruction_height, int orientation, int width, int y) {
        int distance_x = (travel_x + width) - 1;
        int distance_y = (travel_y + obstruction_height) - 1;
        if (x >= travel_x && x <= distance_x && y >= travel_y && y <= distance_y)
            return true;

        if (x == travel_x - 1 && y >= travel_y && y <= distance_y
            && (adjacencies[x - inset_x][y - inset_y] & 8) == 0
            && (orientation & 8) == 0)
            return true;

        if (x == distance_x + 1 && y >= travel_y && y <= distance_y
            && (adjacencies[x - inset_x][y - inset_y] & 0x80) == 0
            && (orientation & 2) == 0)
            return true;

        return y == travel_y - 1 && x >= travel_x && x <= distance_x
            && (adjacencies[x - inset_x][y - inset_y] & 2) == 0
            && (orientation & 4) == 0 || y == distance_y + 1 && x >= travel_x && x <= distance_x
            && (adjacencies[x - inset_x][y - inset_y] & 0x20) == 0
            && (orientation & 1) == 0;
    }
}
