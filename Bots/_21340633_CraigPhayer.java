package Bots;

import robocode.*;

import static robocode.util.Utils.normalRelativeAngle;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class _21340633_CraigPhayer extends AdvancedRobot {
    private int direction = 1;
    private boolean win = true;
    private double enemyNRG;
    private double POWER = .1;
    private int hitCount = 0;
    private int missCount = 0;
    private int hits;

    private ArrayList<Double> energyHistory = new ArrayList<>();

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

        while (true) {
            if (Math.random() > .5) {
                setMaxVelocity((12 * Math.random()) + 12);
            }

            /*if (getVelocity() == 0){
                inactive();
            }*/
            setTurnRight(10000);
            setMaxVelocity(5);
            ahead(10000);

            //Arena wall detection
            if (getX() > getBattleFieldWidth() - 100 || getX() > getBattleFieldHeight() - 100
                    || getY() > getBattleFieldWidth() - 100 || getY() > getBattleFieldHeight() - 100) {
                //Alter direction
                direction *= -1;
            }
        }

    }

    public void onScannedRobot(ScannedRobotEvent event) {
        double absBearing = event.getBearingRadians() + getHeadingRadians();//Enemy absolute bearing
        double latVel = event.getVelocity() * Math.sin(event.getHeadingRadians() - absBearing);//enemies lateral Vel.
        double gun;//Time to set how much we need to turn our gun and give lead

        // Update energy history with current energy level
        energyHistory.add(getEnergy());

        //set random vel.
        if (Math.random() > .5) {
            setMaxVelocity((12 * Math.random()) + 12);
        }

        if (event.isSentryRobot()) {
            setAhead(-90 - event.getBearing());
        } else {//Not a turret
            setTurnRadarLeftRadians(getRadarTurnRemainingRadians());//Radar lock
            if (event.getDistance() > 150) {
                gun = normalRelativeAngle(absBearing - getGunHeadingRadians() + latVel / 22);
                setTurnGunRightRadians(gun); //turn our gun
                setTurnRightRadians(robocode.util.Utils.normalRelativeAngle(absBearing - getHeadingRadians() + latVel / getVelocity()));//drive towards the enemies predicted future location
                setAhead((event.getDistance() - 100) * direction);
                setFire(POWER);

            } else /*if (event.getDistance() <= 25 && getEnergy() > 85)*/ {//Too close to enemy
                //moveDir = -moveDir;
                gun = normalRelativeAngle(absBearing - getGunHeadingRadians() + latVel / 12);
                setTurnGunRightRadians(gun); //turn our gun
                setTurnLeft(-90 - event.getBearing());
                setAhead((event.getDistance() - 140) * direction);
                setFire(POWER);
            }
        }
    }

    public void inactive() {
        while (true) {
            if (getVelocity() == 0) {
                setTurnRight(10000);
                setMaxVelocity(5);
                ahead(10000);
            }
        }

    }

    public void onHitWall(HitWallEvent event) {
        direction *= -1;
    }

    public void onBulletHitBullet(BulletHitBulletEvent event) {
        strafe();

        if (getEnergy() > 30) {
            POWER++;
        } else {

        }
    }

    public void onHitByBullet(HitByBulletEvent event) {
        hits++;

        if (hits >= 2) {
            strafe();
        }
    }


    public void onBulletHit(BulletHitEvent event) {
        int index = energyHistory.size() - 5;
        hitCount++;
        if (event.getEnergy() > 10) {
            energyHistory.add(getEnergy());

            if (hitCount > 3) {
                POWER = 3;
            }
            if (energyHistory.get(index) > getEnergy()) {
                POWER = .1;
                hitCount = 0;
            }
        }

        /*if (energyHistory.get(index) > getEnergy() - 10 && energyHistory.get(index) < getEnergy()) {
            POWER = .1;
            hitCount = 0;
        }*/
        /*if (event.getBullet().isActive()) {
            POWER = 3;
            if (event.getEnergy() < 10) {
                POWER = 2;
            }
        }*/
    }

    public void onBulletMissed(BulletMissedEvent event) {
        missCount++;
        if (missCount % 2 == 1) {
            POWER = .1;
        }
    }

    public void onRobotDeath(RobotDeathEvent event) {
        POWER = .1;
    }

    public void onHitRobot(HitRobotEvent event) {
        direction *= -1;
        setAhead(100);
    }

    public void onWin(WinEvent event) {

        while (win) {
            turnLeft(5);
            setAhead(20);
            turnRight(5);
            setAhead(-20);
        }
    }

    public void strafe() {// strafe by changing direction every "randomInt" ticks
        Random rand = new Random();
        int randomInt = rand.nextInt(21) + 5;
        if (getTime() % randomInt == 0) {
            direction *= -1;
            setAhead(150 * -direction);
        }
    }
}

/*// strafe by changing direction every "randomInt" ticks
            Random rand = new Random();
            int randomInt = rand.nextInt(21) + 5;
            if (getTime() % randomInt == 0) {
                moveDir *= -1;
                setAhead(150 * -moveDir);
            }*/
