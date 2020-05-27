//PercolationVisualizerAssist
//Josh Hug - 02/07/12

//Draws an N by N grid with different colored squares
public class PercolationVisualizerAssist {
    public static void main(String [] args)
    {
        //Number of squares to draw
        int N = 9;
     
        //set axes
        //After the lines below are executed, then the top right of the draw window
        //will be at coordinate (N,N). 
        StdDraw.setXscale(0, N);
        StdDraw.setYscale(0, N);

        double SQUARE_SIZE = 0.45;
        
        //Sets the draw window to be all black
        StdDraw.clear(StdDraw.BLACK);
        
        //draw an NxN grid
        for (int column = 1; column <= N; column++)
            for (int row = 1; row <= N; row++)
            {
                // map column from domain [1, N] to range [0.5, N-0.5]
                // Since square size is 0.45, this uses most of the available screen
                // space, leaving a small gap between squares and at the edges
                double x = (row - 0.5);

                // map row from domain [1, N] to range [(N - 1 + 0.5), 0.5]
                double y = (N - column + 0.5);

                //conditionally sets the pen color to be light blue or white
                if (column > row)
                        StdDraw.setPenColor(StdDraw.BOOK_LIGHT_BLUE);
                    else
                        StdDraw.setPenColor(StdDraw.WHITE);

                //draws a filled square centered at x,y using the current pen color
                StdDraw.filledSquare(x, y, SQUARE_SIZE);
            }
    }
}
