package nu.geeks.paceofspace.util;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by hannespa on 15-07-31.
 */
public class mMath {

    /**
     * Checks if the mouse overlaps any given position, with a bounding box of distance
     *
     * @param mousePos mouse position
     * @param hitPoint position of the object you want to check for a hit.
     * @param distance the half side of the bounding box
     * @return true for hit
     */
    public boolean hitCheck(Vector2 mousePos, Vector2 hitPoint, float distance){

        if(		mousePos.x > (hitPoint.x - distance) &&
                mousePos.x < (hitPoint.x + distance) &&
                mousePos.y > (hitPoint.y - distance) &&
                mousePos.y < (hitPoint.y + distance)
                ){
            return true;
        }else{
            return false;
        }

    }

}
