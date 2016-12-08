package com.sidm.mymgp;

/**
 * Created by User on 8/12/2016.
 */

public class intersection
{
    Vector2D one, two, three, four;
    public intersection(Fingerline one1, Fingerline two1)
    {
        one = new Vector2D(one1.getStartX(), one1.getStartY());
        two = new Vector2D(one1.getEndX(),one1.getEndY());
        three = new Vector2D(two1.getStartX(),two1.getStartY());
        four = new Vector2D(two1.getEndX(),two1.getEndY());
    }

    boolean algebraNonsense()
    {
        float m = ((one.y - two.y) / (one.x - two.x));
        float c = (one.y - (m * one.x));

        float m2 = ((four.y - three.y) / (four.x - three.x));
        float c2 = (three.y - (m * three.x));

        float IPX = 0, IPY = 0;
        float IPYC1 = 0, IPYC2 = 0;
        Vector2D intersectionPoint = new Vector2D(IPX, IPY);

        lineEQ line1 = new lineEQ(m, one.x, c);
        lineEQ line2 = new lineEQ(m2, three.x, c2);

        if(line1.m == (line2.m * -1))
        {
           IPX = (line2.c - line1.c) / (line1.m - line2.m);
           IPYC1 = (line1.m * IPX + line1.c);
           IPYC2 = (line2.m * IPX + line2.c);
           if(IPYC1 == IPYC2)
           {
               IPY = IPYC1;
           }
        }
        if(IPX != 0 && IPY != 0)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

  //boolean onSegment(Vector2D p, Vector2D q, Vector2D r)
  //{
  //    if (q.x <= java.lang.Math.max(p.x, r.x) && q.x >= java.lang.Math.min(p.x, r.x) &&
  //            q.y <= java.lang.Math.max(p.y, r.y) && q.y >= java.lang.Math.min(p.y, r.y))
  //        return true;

  //    return false;
  //}
  //float orientation(Vector2D p, Vector2D q, Vector2D r)
  //{
  //    // See http://www.geeksforgeeks.org/orientation-3-ordered-points/
  //    // for details of below formula.
  //    float val = (q.y - p.y) * (r.x - q.x) -
  //            (q.x - p.x) * (r.y - q.y);

  //    if (val == 0) return 0;  // colinear

  //    return (val > 0)? 1: 2; // clock or counterclock wise
  //}
  //boolean doIntersect()
  //{
  //    // Find the four orientations needed for general and
  //    // special cases
  //    float o1 = orientation(one, two, three);
  //    float o2 = orientation(one, two, four);
  //    float o3 = orientation(three, four, one);
  //    float o4 = orientation(three, four, two);

  //    // General case
  //    if (o1 != o2 && o3 != o4)
  //        return true;

  //    // Special Cases
  //    //// one, two and three are colinear and three lies on segment onethree
  //    //if (o1 == 0 && onSegment(one, three, two)) return true;
//
  //    //// one, two and three are colinear and four lies on segment onetwo
  //    //if (o2 == 0 && onSegment(one, four, two)) return true;
//
  //    //// three, four and one are colinear and one lies on segment threefour
  //    //if (o3 == 0 && onSegment(three, one, four)) return true;
//
  //    //// three, four and two are colinear and two lies on segment threefour
  //    //if (o4 == 0 && onSegment(three, two, four)) return true;

  //    return false; // Doesn't fall in any of the above cases
  //}

}
