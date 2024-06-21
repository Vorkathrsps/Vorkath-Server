package com.cryptic.cache.definitions;

import com.cryptic.model.map.region.Buffer;
import com.cryptic.network.codec.RSBuffer;
import com.google.common.collect.Maps;
import io.netty.buffer.Unpooled;
import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jonathan on 6/14/17.
 */
public class AnimationDefinition implements Definition {

    private int id;
    public int framestep = -1;
    public boolean oneSq = false;
  public int forcePrio = 5;
    public int leftHandItem = -1;
    public int rightHandItem = -1;
    public boolean replay = false;
    public int priority = 5;
   public int delayType = 2;
    public int[] delays;
    int[] flowdata;
    public int[] skeletonSets;
    int[] frame2Ids;
    public int animMayaID = -1;
    public Map<Integer, Sound> animMayaFrameSounds;
    public int animMayaStart;
    public int animMayaEnd;
    public boolean[] animMayaMasks;
    public Sound[] sounds;
    public int loopCount = 99;
    public int moveStyle = -1;
    public int idleStyle = -1;
    private int skeletalId = -1;

    public AnimationDefinition(int id, byte[] data) {
        this.id = id;

        if (data != null && data.length > 0)
            decode(new RSBuffer(Unpooled.wrappedBuffer(data)));
    }

    void decode(RSBuffer buffer) {
        while (true) {
            int op = buffer.readUByte();
            if (op == 0)
                break;
            decodeNext(buffer, op);
        }
    }

    void decodeNext(RSBuffer buffer, int opcode) {
        if (opcode == 1) {
            int frameCount = buffer.readUShort();
            delays = new int[frameCount];

            for(int index = 0; index < frameCount; ++index) {
                delays[index] = buffer.readUShort();
            }

            skeletonSets = new int[frameCount];

            for(int index = 0; index < frameCount; ++index) {
                skeletonSets[index] = buffer.readUShort();
            }

            for(int index = 0; index < frameCount; ++index) {
                skeletonSets[index] += buffer.readUShort() << 16;
            }

        } else if (opcode == 2) {
            this.framestep = buffer.readUShort();
        } else if (opcode == 3) {
            int count = buffer.readUByte();
            this.flowdata = new int[count + 1];
            for(int index = 0; index < count; ++index) {
                this.flowdata[index] = buffer.readUByte();
            }
            flowdata[count] = 0x98967f;
        } else if (opcode == 4) {
            this.oneSq = true;
        } else if (opcode == 5) {
            this.priority = buffer.readUByte();
        } else if (opcode == 6) {
            this.leftHandItem = buffer.readUShort();
        } else if (opcode == 7) {
            this.rightHandItem = buffer.readUShort();
        } else if (opcode == 8) {
            this.loopCount = buffer.readUByte();
            this.replay = true;
        } else if (opcode == 9) {
            this.moveStyle = buffer.readUByte();
        } else if (opcode == 10) {
            this.idleStyle = buffer.readUByte();
        } else if (opcode == 11) {
            this.delayType = buffer.readUByte();
        } else if (opcode == 12) {
            int count = buffer.readUByte();
            this.frame2Ids = new int[count];

            for(int index = 0; index < count; ++index) {
                this.frame2Ids[index] = buffer.readUShort();
            }

            for(int index = 0; index < count; ++index) {
                this.frame2Ids[index] += buffer.readUShort() << 16;
            }
        } else if (opcode == 13) {
            int var11;
            int var17;
            int var18;
            int var19;

            int var3 = buffer.readUByte();
            this.sounds = new Sound[var3];

            for (int var4 = 0; var4 < var3; ++var4) {
                Sound var13;
                Sound[] var14;
                label163: {
                    var14 = this.sounds;
                    var17 = buffer.readUShort();
                    var18 = buffer.readUByte();
                    var19 = buffer.readUByte();
                    var11 = buffer.readUByte();


                    if (var17 >= 1 && var18 >= 1 && var19 >= 0 && var11 >= 0) {
                        var13 = new Sound(var17, var18, var19, var11);
                        break label163;
                    }

                    var13 = null;
                }

                var14[var4] = var13;
            }
        } else if (opcode == 14) {
            this.skeletalId = buffer.readInt();
        } else if (opcode == 15) {
            int var3 = buffer.readUShort();
            this.animMayaFrameSounds = new HashMap<>();

            for (int var4 = 0; var4 < var3; ++var4) {
                int var5;
                Sound var6;
                label177: {
                    var5 = buffer.readUShort();
                    boolean var7 = false;
                    int var16;

                    var16 = buffer.readUShort();
                    int var17 = buffer.readUByte();
                    int var18 = buffer.readUByte();
                    int var19 = buffer.readUByte();


                    if (var16 >= 1 && var17 >= 1 && var18 >= 0 && var19 >= 0) {
                        var6 = new Sound(var16, var17, var18, var19);
                        break label177;
                    }

                    var6 = null;
                }

                this.animMayaFrameSounds.put(var5, var6);
            }
        } else if (opcode == 16) {
            this.animMayaStart = buffer.readUShort();
            this.animMayaEnd = buffer.readUShort();
        } else if (opcode == 17) {
            this.animMayaMasks = new boolean[256];

            Arrays.fill(this.animMayaMasks, false);

            int count = buffer.readUByte();

            for(int index = 0; index < count; ++index) {
                this.animMayaMasks[buffer.readUByte()] = true;
            }
        }
    }

    static class Sound {
        public int field2107;
        public int field2108;
        public int field2109;
        public int field2113;
        public Sound(int var1, int var2, int var3, int var4) {
            this.field2107 = var1;
            this.field2108 = var2;
            this.field2109 = var3;
            this.field2113 = var4;
        }
    }
}
