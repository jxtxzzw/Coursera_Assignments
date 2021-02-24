# University of Washington, Programming Languages, Homework 6, hw6runner.rb

# This is the only file you turn in, so do not modify the other files as
# part of your solution.

class MyPiece < Piece
  # The constant All_My_Pieces should be declared here

  # the rotation should be centered, i.e., it should be [0,0], otherwise [[0,0],[1,0],[2,0]] rotates to [[0,0],[0,1],[0,2]] or [[-2,0],[-1,0],[0,0]]
  All_My_Pieces = All_Pieces + [
    [
     [[0, 0], [1, 0], [-1, 0], [2, 0], [-2, 0]],
     [[0, 0], [0, 1], [0, 2], [0, -1], [0, -2]]
    ],
    rotations([[0, 0], [1, 0], [0, 1]]),
    rotations([[0, 0], [1, 0], [0, 1], [1, 1], [-1, 0]]) 
  ]

  # cheat_piece_well_formed: The cheat piece should be a 3-deep nested array.
  Cheat_Piece = [[[0, 0]]]

  # your enhancements here

  def self.next_piece(board)
    MyPiece.new(All_My_Pieces.sample, board)
  end

  def self.cheat_piece(board)
    MyPiece.new(Cheat_Piece, board)
  end

end

class MyBoard < Board
  # your enhancements here

  def initialize (game)
    super(game)
    @cheating = false
  end

  def cheat
  	if @score >= 100 and !@cheating
  		@cheating = true
  		@score -= 100
  	end
  end

  def next_piece
  	if @cheating
  		@current_block = MyPiece.cheat_piece(self)
  		@cheating = false
  	else
  		@current_block = MyPiece.next_piece(self)
  	end
  	@current_pos = nil
  end

  def store_current
    locations = @current_block.current_rotation
    displacement = @current_block.position
    # to override store_current in MyBoard because the provided code is broken if a Tetris piece does not have exactly 4 blocks in it.
    (0..(locations.size - 1)).each{|index| 
      current = locations[index];
      @grid[current[1]+displacement[1]][current[0]+displacement[0]] =  @current_pos[index]
    }
    remove_filled
    @delay = [@delay - 2, 80].max
  end

end

class MyTetris < Tetris
  # enhancements here

  def set_board
  	# cannot use super, because we do not want a @board = Board.new() and draw another board
    @canvas = TetrisCanvas.new
    @board = MyBoard.new(self)
    @canvas.place(@board.block_size * @board.num_rows + 3,
                  @board.block_size * @board.num_columns + 6, 24, 80)
    @board.draw
  end

  def key_bindings
    super
    @root.bind('u' , proc {
    	@board.rotate_clockwise
      @board.rotate_clockwise
    }) 
    @root.bind('c' , proc {
    	@board.cheat
    })
    end
end

