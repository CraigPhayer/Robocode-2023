package Bots;

import robocode.*;

import java.awt.*;

public class MyBot extends AdvancedRobot{
    private int moveDir = 1;

    public void run(){
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

    public void onScannedRobot(ScannedRobotEvent event){
        double absBearing = event.getBearingRadians() + getHeadingRadians();//Enemy absolute bearing

        setTurnRadarLeftRadians(getRadarTurnRemainingRadians());//Radar lock

        //Arena wall detection
        if (getBattleFieldWidth() - getX() == 20 || getBattleFieldHeight() - getY() == 20){
            //Alter direction
            setAhead( 100*moveDir );
            moveDir *= -1;

            // always square off against our enemy
            setTurnRight(event.getBearing() + 90);

            // strafe by changing direction every 20 ticks
            if (getTime() % 20 == 0) {
                moveDir *= -1;
                setAhead(150 * moveDir);
            }

        }
    }

}
