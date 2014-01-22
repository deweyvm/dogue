host=doge@dogue.in
file=build-server.py

scp $file $host:whatever/
ssh $host "cd whatever && ./build-server-run"
