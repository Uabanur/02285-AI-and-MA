
$SearchStrategy = "-greedy"
$ShowGUI = $true;

$Levels = @(
    'SAFirefly'
    , 'SACrunch'
    , 'SAsoko3Mix2_08'
    #, 'SAsokobanLevel96'
    , 'SAsolo'
    #, 'SAsorting'
    , 'SATheRedDot'
    , 'SAtowersOfHoChiMinh03'
    , 'SAtowersOfSaigon03'
    , 'SAWatsOn'
    # 'BFSFriendly'
    #  'MAPF00'
    #, 'MAPF01'
    # , 'MAPF02'
    # , 'MAPF02C'
    # , 'MAPF03'
    # , 'MAPF03C'
    # , 'MAPFslidingpuzzle'
    # , 'MAPFreorder2'
    );

try
{
    pushd searchclient

    echo "Removing old class files"
    rm searchclient/*.class

    echo "Compiling class files"
    javac searchclient/*.java

    echo "Running mavis"
    foreach($level in $Levels){
        echo "`n`n`t## Level: $level ##`n"
        if ($ShowGUI){
            java -jar mavis.jar -l "levels/$level.lvl" -c "java searchclient.SearchClient $SearchStrategy" -g -s 75
        } else {
            java -jar mavis.jar -l "levels/$level.lvl" -c "java searchclient.SearchClient $SearchStrategy"
        }
    }
}
finally
{
    popd
}