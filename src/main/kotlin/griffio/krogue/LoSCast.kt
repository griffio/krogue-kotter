//package griffio.krogue
//
//import kotlin.math.roundToInt
//import kotlin.math.sqrt
//
//
//object LoSCast {
//
//    data class Point(val x: Int, val y: Int)
//
//    fun Point.lineBetween(lower: Point, upper: Point): Boolean {
//        //Dot product of upper rotated ccw by pi/2
//        val upperComponent = this.y * upper.x - this.x * upper.y;
//        //Dot product of lower rotated cw by pi/2
//        val lowerComponent = this.x * lower.y - this.y * lower.x;
//        return upperComponent > 0 && lowerComponent > 0
//    }
//
//    fun renderHeroRadius(heroX: Int, heroY: Int, view: List<MutableList<Tile>>, radius: Int) {
//        castLight(view, heroX, heroY, radius)
//    }
//
//    @OptIn(ExperimentalStdlibApi::class)
//    private fun castLight(
//        view: List<MutableList<Tile>>, heroX: Int, heroY: Int, viewRadius: Int,
//    ) {
//
//        val worldMaxX = view[0].size - 1
//        val worldMaxY = view.size - 1
//        var x = heroX
//        var y = heroX
//
//        for (i in 1..viewRadius) {
//            x -= 0
//            y -= 1
//            view[y][x].isVisible = true
//            view[y][x].glyph = '.'
//            x -= 0
//            y -= -1
//            view[y][x].isVisible = true
//            view[y][x].glyph = '.'
//            x -= 1
//            y -= 0
//            view[y][x].isVisible = true
//            view[y][x].glyph = '.'
//            x -= 1
//            y -= -1
//            view[y][x].isVisible = true
//            view[y][x].glyph = '.'
//        }
//    }
//
//    private fun distance(x1: Int, x2: Int, y1: Int, y2: Int): Int {
//        val beforeRoot = ((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2))
//        return if (beforeRoot > 0) {
//            sqrt(beforeRoot.toDouble()).roundToInt()
//        } else if (beforeRoot < 0) {
//            sqrt(-beforeRoot.toDouble()).roundToInt()
//        } else {
//            0
//        }
//    }
//
//}
//
////void LOS(bool force)
////{
////    if (player.HasMoved() || force)
////    {
////        ClearRayMap();
////        for (int x = player.X() - VIEW_RADIUS; x < scene.GetLength(1); x++)
////        {
////            for (int y = player.Y() - VIEW_RADIUS; y < scene.GetLength(0); y++)
////            {
////                if (Distance(player.X(), x, player.Y(), y) <= VIEW_RADIUS)
////                {
////                    try
////                    {
////                        if (HasLOS(new Point(x, y)))
////                        {
////                            RayMap[y, x] = true;
////                            HasSeen[y, x] = true;
////                        }
////                        else
////                        {
////                            RayMap[y, x] = false;
////                        }
////                    }
////                    catch
////                    {
////                        //block is off the screen, trycatch needed to prevent error
////                    }
////                }
////
////            }
////        }
////    }
////}
////
////bool HasLOS(Point blockpoint)
////{
////    bool blocked = true;
////    for (float i = 0; i < 1; i += 0.05f)
////    {
////        yvect = (int)GetVector(new Vector2(blockpoint.X, blockpoint.Y), new Vector2(player.X(), player.Y()), i).Y;
////        xvect = (int)GetVector(new Vector2(blockpoint.X, blockpoint.Y), new Vector2(player.X(), player.Y()), i).X;
////        if (currentlevel[yvect, xvect] == BLOCKING_OBJECT)
////        {
////            blocked = false;
////        }
////    }
////    return blocked;
////}
////
////Vector2 GetVector(Vector2 vect1, Vector2 vect2, float t)
////{
////    Vector2 delta = vect2 - vect1;
////    float distance = delta.Length();
////    if (distance == 0.0f)
////    {
////        return vect1;
////    }
////    else
////    {
////        Vector2 direction = delta / distance;
////        return vect1 + direction * (distance * t);
////    }
////}
////
////int Distance(int x1, int x2, int y1, int y2)
////{
////    int beforeroot = ((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
////    if (beforeroot > 0)
////    {
////        return (int)Math.Sqrt(beforeroot);
////    }
////    else if (beforeroot < 0)
////    {
////        return (int)Math.Sqrt(-beforeroot);
////    }
////    else
////    {
////        return 0;
////    }
////}
