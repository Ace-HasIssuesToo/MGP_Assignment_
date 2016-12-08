Line
- Activates only when in a grid circle/point
- Disappears if grid line does not end in another point
- Draws another line and can change direction when reach the next point
- Starts from middle of grid point, use event.X - bitmap.width / 2

Score for triangle shape(not ngon i know)
- Array of Array of grid circles to store near proximity waypoints
- Using starting circle, find the two nearest points and insert all three into the array
- If player somehow checks through all three, gain three points for 3 sided polygon

Alternatively, each grid circle object can contain two pointer references to two nearest proximity circles.
If player checks through all three, same thing

Menu flow
- splashscreen -> sponsor screen -> main menu -> level selection