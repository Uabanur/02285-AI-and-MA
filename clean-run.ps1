pushd searchclient

echo "Removing old class files"
rm searchclient/*.class

echo "Compiling class files"
javac searchclient/*.java

echo "Running mavis"
java -jar mavis.jar -l levels/SAD1.lvl -c "java searchclient.SearchClient" -g -s 150 -t 180

popd