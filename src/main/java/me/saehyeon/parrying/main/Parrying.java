package me.saehyeon.parrying.main;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class Parrying {

    public static final float SCAN_RANGE = 1;
    public static final float MAX_PARRYING_REACH = 4;

    /*
        반경 내의
     */
    public static final float PARRYING_CHECK_SENSITIVITY = 0.5f;

    public void tryParrying(Player player) {

        // 페링 대상이 되는 화살들
        ArrayList<Entity> arrows = getArrows(player);

        // 화살이 아닌 엔티티들 제거
        arrows.removeIf(e -> !(e instanceof Arrow));

        // 플레이어가 보고 있지 않은 곳에 있는 화살 제거
        arrows.removeIf(e -> !checkRightArrow(e,player));

        // 화살 페링
        for(Entity arrow : arrows) {

            // 화살의 가속도를 반대로
            arrow.setVelocity(arrow.getVelocity().multiply(-0.7));

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
