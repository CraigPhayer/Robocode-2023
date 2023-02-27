package Bots;

import robocode.*;
import static robocode.util.Utils.normalRelativeAngle;

import java.awt.*;
import java.util.Random;

public class MyBot extends AdvancedRobot {
    private int moveDir = 1;
    private boolean win = true;
    private double enemyNRG;

    public void run() {
        //Radar
        setAdjustRadarForRobotTurn(true);//keep the radar still while we turn
        turnRadarRightRadians(Double.POSITIVE_INFINITY);

        //Colour
        setRadarColor(Color.black);
        setBodyColor(Color.black);
        setGunColor(Color.black);
        setScanColor(Color.pink);
        setBulletColor(Color.green);

        //Gun
        setAdjustGunForRobotTurn(true);//Keep gun Static while we turn

    }

    public void onScannedRobot(ScannedRobotEvent event) {
        double absBearing = event.getBearingRadians() + getHeadingRadians();//Enemy absolute bearing
        double latVel = event.getVelocity() * Math.sin(event.getHeadingRadians() - absBearing);//enemies lateral Vel.

        setTurnRadarLeftRadians(getRadarTurnRemainingRadians());//Radar lock
        setMaxVelocity((12 * Math.random()) + 12);

        //Arena wall detection
        if (getBattleFieldWidth() - getX() == 20 || getBattleFieldHeight() - getY() == 20) {
            //Alter direction
            moveDir *= -1;
            setAhead(100 * moveDir);
        }

        double gun;//Time to set how much we need to turn our gun and give lead
        if (event.getDistance() > 150) {
            gun = normalRelativeAngle(absBearing - getGunHeadingRadians()+latVel/22);
            setTurnGunRightRadians(gun); //turn our gun
            setTurnRightRadians(robocode.util.Utils.normalRelativeAngle(absBearing-getHeadingRadians()+latVel/getVelocity()));//drive towards the enemies predicted future location
            setAhead((event.getDistance() - 130)*moveDir);
            setFire(3);

        } else /*if (event.getDistance() <= 10)*/ {//Too close to enemy
            //moveDir = -moveDir;
            gun = normalRelativeAngle(absBearing - getGunHeadingRadians()+latVel/12);
            setTurnGunRightRadians(gun); //turn our gun
            setTurnLeft(-90 - event.getBearing());
            setAhead((event.getDistance() - 140)*moveDir);
            setFire(3);

            /*// strafe by changing direction every "randomInt" ticks
            Random rand = new Random();
            int randomInt = rand.nextInt(21) + 5;
            if (getTime() % randomInt == 0) {
                //moveDir *= -1;
                setAhead(150 * -moveDir);
            }*/
        }

    }

    public void onHitWall(HitWallEvent event) {
        moveDir = -moveDir;
    }

    public void onWin(WinEvent event){

        while (win){
            turnLeft(5);
            setAhead(20);
            turnRight(5);
            setAhead(-20);
        }
    }
}
