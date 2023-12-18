    
function a = smallTest()
    I = [   
 34.0 85.0 187.0 255.0 34.0 85.0 187.0 255.0 34.0 85.0 187.0 255.0 34.0 85.0 187.0 255.0 ;
68.0 0.0 0.0 0.0 68.0 0.0 0.0 0.0 68.0 0.0 0.0 0.0 68.0 0.0 0.0 0.0 ;
102.0 0.0 0.0 0.0 102.0 0.0 0.0 0.0 102.0 0.0 0.0 0.0 102.0 0.0 0.0 0.0 ;
136.0 0.0 0.0 255.0 136.0 0.0 0.0 255.0 136.0 0.0 0.0 255.0 136.0 0.0 0.0 255.0 ;
34.0 85.0 187.0 255.0 34.0 85.0 187.0 255.0 34.0 85.0 187.0 255.0 34.0 85.0 187.0 255.0; 
68.0 0.0 0.0 0.0 68.0 0.0 0.0 0.0 68.0 0.0 0.0 0.0 68.0 0.0 0.0 0.0 ;
102.0 0.0 0.0 0.0 102.0 0.0 0.0 0.0 102.0 0.0 0.0 0.0 102.0 0.0 0.0 0.0 ;
136.0 0.0 0.0 255.0 136.0 0.0 0.0 255.0 136.0 0.0 0.0 255.0 136.0 0.0 0.0 255.0 ;
34.0 85.0 187.0 255.0 34.0 85.0 187.0 255.0 34.0 85.0 187.0 255.0 34.0 85.0 187.0 255.0 ;
68.0 0.0 0.0 0.0 68.0 0.0 0.0 0.0 68.0 0.0 0.0 0.0 68.0 0.0 0.0 0.0 ;
102.0 0.0 0.0 0.0 102.0 0.0 0.0 0.0 102.0 0.0 0.0 0.0 102.0 0.0 0.0 0.0 ;
136.0 0.0 0.0 255.0 136.0 0.0 0.0 255.0 136.0 0.0 0.0 255.0 136.0 0.0 0.0 255.0 ;
34.0 85.0 187.0 255.0 34.0 85.0 187.0 255.0 34.0 85.0 187.0 255.0 34.0 85.0 187.0 255.0 ;
68.0 0.0 0.0 0.0 68.0 0.0 0.0 0.0 68.0 0.0 0.0 0.0 68.0 0.0 0.0 0.0 ;
102.0 0.0 0.0 0.0 102.0 0.0 0.0 0.0 102.0 0.0 0.0 0.0 102.0 0.0 0.0 0.0 ;
136.0 0.0 0.0 255.0 136.0 0.0 0.0 255.0 136.0 0.0 0.0 255.0 136.0 0.0 0.0 255.0   ];
    
     I = im2double(I);
     I = I-128;
    T = dctmtx(8);
    dct = @(block_struct) T * block_struct.data * T';
    B = blockproc(I,[8 8],dct)
end

function a = bigTest()
    % Dimensions of the matrix
    rows = 3840;
    cols = 2160;
    
    % Create a matrix where each element is its row index
    X = repmat((1:rows)', 1, cols);
    
    % Create a matrix where each element is its column index
    Y = repmat(1:cols, rows, 1);
    
    % Calculate the value for each element
    matrix = mod(X + Y * 8, 256);
    
    
    f = @() mdct(matrix);
    timeit(f)
end

function a = mdct(matrix)
    I = im2double(matrix);
    I = I-128;
    T = dctmtx(8);
    dct = @(block_struct) T * block_struct.data * T';
    a = blockproc(I,[8 8],dct); 
end