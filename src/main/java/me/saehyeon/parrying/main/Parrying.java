package me.saehyeon.parrying.main;

import org.bukkit.*;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;

public class Parrying {

    // 페링 시도 쿨타임인 플레이어 배열
    ArrayList<Player> tryCoolTime = new ArrayList<>();

    // 페링 쿨타임 (초)
    public static final Long TRY_COOLTIME = 1L;

    // 검사할 위치에서 화살을 인식할 반경
    public static final float SCAN_RANGE = 1;

    // 페링할 화살을 찾는 최대 거리
    public static final float MAX_PARRYING_REACH = 2;

    // 페링할 화살을 찾는 반복문에서의 증가폭
    public static final float PARRYING_CHECK_SENSITIVITY = 0.5f;

    // 페링 시의 반동 (뒤로 밀려남)
    public static final float RECOIL = -0.5f;

    public void tryParrying(Player player) {

        // 페링 확인 쿨타임 상태라면 페링이 되지 않음.
        if(tryCoolTime.contains(player))
            return;

        // 페링 시도 쿨타임 해제
        Bukkit.getScheduler().runTaskLater(Main.instance, () -> tryCoolTime.remove(player), TRY_COOLTIME*20);

        // 페링 대상이 되는 화살들
        ArrayList<Entity> arrows = getArrows(player);

        // 화살이 아닌 엔티티들 제거
        arrows.removeIf(e -> !(e instanceof Arrow));

        // 플레이어가 보고 있지 않은 곳에 있는 화살 제거
        arrows.removeIf(e -> !checkRightArrow(e,player));

        // 가속도가 붙지 않은 화살 제거
        arrows.removeIf(Entity::isOnGround);

        // 화살 페링
        for(Entity arrow : arrows) {

            // 화살의 가속도 설정
            arrow.setVelocity(player.getLocation().getDirection());

            // 페링 반동
            player.setVelocity(player.getLocation().getDirection().multiply(RECOIL));

            // 효과음
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, SoundCategory.MASTER, 1,2);

            // 파티클 소환
            Location particleLoc = player.getEyeLocation().clone().add(player.getLocation().getDirection().normalize());
            player.getWorld().spawnParticle(Particle.BLOCK_CRACK, particleLoc,3, getWeaponMaterial(player.getInventory().getItemInMainHand()).createBlockData());


        }
    }

    /**
     * 플레이어의 앞에 있는 화살들을 반환하는 메소드
     * 기본적으로 이 메소드에서 반환된 화살들이 페링됩니다.
     */
    ArrayList<Entity> getArrows(Player player) {

        boolean debug = true;

        ArrayList<Entity> arrows = new ArrayList<>();

        for(float i = 1; i < MAX_PARRYING_REACH; i += PARRYING_CHECK_SENSITIVITY) {
            Vector dir          = player.getLocation().getDirection();

            // 검사할 위치를 앞으로 나아가게 하기
            Location scanLoc    = player.getLocation().clone().add(dir.multiply(i)).add(0,1,0);

            //player.getWorld().spawnParticle(Particle.HEART, scanLoc, 1);

            // 검사할 위치로 부터 SCAN_RANGE 이내의 화살들은 페링되는 것으로 간주
            arrows.addAll( player.getWorld().getNearbyEntities(scanLoc,SCAN_RANGE,SCAN_RANGE,SCAN_RANGE) );
        }

        return arrows;

    }

    /**
     * 들고 있는 아이템의 종류에 따라 Block Crack 파티클 소환을 위한 Material를 반환합니다.
     * 예를들어, 다이아몬드 검이라면 다이아몬드 블럭 Material를 반환합니다.
     */
    Material getWeaponMaterial(ItemStack itemstack) {
        String typeStr = itemstack.getType().toString().toLowerCase();

        if(typeStr.contains("diamond"))
            return Material.DIAMOND_BLOCK;

        if(typeStr.contains("iron"))
            return Material.IRON_BLOCK;

        if(typeStr.contains("golden"))
            return Material.GOLD_BLOCK;

        if(typeStr.contains("netherite"))
            return Material.NETHERITE_BLOCK;

        if(typeStr.contains("wooden"))
            return Material.OAK_LOG;

        return Material.ANVIL;
    }

    /**
     * 페링되기에 올바른 화살인지 확인하는 메소드
     * @return 페링되기에 적합한 화살이라면 true, 아니라면 false 반환
     */
    boolean checkRightArrow(Entity arrow, Player player) {
        double frontDistance = player.getLocation().distance(arrow.getLocation());
        double backDistance = player.getLocation().clone().add(player.getLocation().getDirection()).distance(arrow.getLocation());

        // 만약 화살의 거리가 플레이어의 등 뒤보다 플레이어의 앞과 가깝다면 적합한 화살임.
        return frontDistance > backDistance;
    }
}
