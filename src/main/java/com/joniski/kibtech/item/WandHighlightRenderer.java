package com.joniski.kibtech.item;

import java.util.List;

import com.joniski.kibtech.KibTech;
import com.joniski.kibtech.item.ModItems;
import com.joniski.kibtech.item.custom.RobotWandItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

@EventBusSubscriber(modid = KibTech.MODID, value = Dist.CLIENT)
public class WandHighlightRenderer {

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player == null || minecraft.level == null){
            return;
        } 

        if (!(player.getMainHandItem().getItem() instanceof RobotWandItem wandItem)) {
            return;
        }

        if (wandItem.setRobot == null){
            return;
        }

        if (!(minecraft.hitResult instanceof BlockHitResult blockHitResult)){
            return;
        }

        Vec3 targetPos = blockHitResult.getBlockPos().getCenter();

        if (targetPos == null){
            return;
        }

        PoseStack poseStack = event.getPoseStack();
        double camX = minecraft.gameRenderer.getMainCamera().getPosition().x;
        double camY = minecraft.gameRenderer.getMainCamera().getPosition().y;
        double camZ = minecraft.gameRenderer.getMainCamera().getPosition().z;

        List<BlockPos> blocks;
        BlockPos startPos;
        BlockPos endPos;

        if (wandItem.getStartSearch() == null){
            startPos = blockHitResult.getBlockPos();
            endPos = blockHitResult.getBlockPos().offset(1,1,1);
        }else{
            blocks = RobotWandItem.getLowestAndHigheBlockPos(wandItem.getStartSearch(), blockHitResult.getBlockPos());
            startPos = blocks.get(0);
            endPos = blocks.get(1);
        }

        poseStack.pushPose();
        poseStack.translate(-camX, -camY, -camZ);

        MultiBufferSource.BufferSource buffer = minecraft.renderBuffers().bufferSource();

        PoseStack.Pose pose = poseStack.last();
        VertexConsumer consumer = buffer.getBuffer(RenderType.lines());

        LevelRenderer.renderLineBox(
            poseStack,
            consumer,
            startPos.getX(), startPos.getY(), startPos.getZ(),
            endPos.getX(), endPos.getY(), endPos.getZ(),
            1.0F, 1.0F, 1.0F, 1.0F
        ); 

        poseStack.popPose();
    }
}