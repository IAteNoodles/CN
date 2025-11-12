# Create a new simulation
set ns [new Simulator]

#Define color
$ns color 1 blue
$ns color 2 red

# Open trace and namfile
set ntrace [open prg2.tr w]
$ns trace-all $ntrace
set namfile [open prg2.nam w]
set namtrace-all $namfile

# Create 6 nodes using a for loop

for { set i 0} {$i<6} {incr i} {
	set n($i) [$ns node]
}

# Create a duplex-link with the nodes

for {set j 0} {$j<5} {incr j} {
	$ns duplex-link $n($j) $n([expr ($j+1)]) 0.1Mb 10ms DropTail
}

# Create congestion point between n2 and n3

$ns queue-limit $n(2) $n(3) 2

# Define Finish
proc Finish {} {
	global ns namfile ntrace
	$ns flush-trace
	close $ntrace
	close $namfile
	exec nam prg2.nam &
	puts "Number of dropped packets"
	exec grep "^d" prg2.tr | cut -d " " -f 5 | grep -c "ping" &
	exit 0
	}

#Define the recv function 
Agent/Ping instproc recv {from rtt} {
	$self instvar node_
	puts "nodes [$node_ id] received ping answer $from with round trip time $rtt ms"
}

# Create ping agents, and attach at n0 and n5
set p0 [new Agent/Ping]
$p0 set class_ 1
$ns attach-agent $n(0) $p0
set p1 [new Agent/Ping]
$p1 set class_ 2
$ns attach-agent $n(5) $p1

$ns connect $p0 $p1
$ns duplex-link-op $n(2) $n(3) queuePos 0.5

#Create congestion
set tcp0 [new Agent/TCP]
$tcp0 set class_ 2
$ns attach-agent $n(2) $tcp0

set sink0 [new Agent/TCPSink]
$ns attach-agent $n(4) $sink0
$ns connect $tcp0 $sink0

#Apply CBR over TCP
set cbr0 [new Application/Traffic/CBR]
$cbr0 set packetSize_ 500
$cbr0 set rate_ 1Mb
$cbr0 attach-agent $tcp0

#Schedule events
$ns at 0.2 "$p0 send"
$ns at 0.4 "$p1 send"
$ns at 0.4 "$cbr0 start"
$ns at 0.8 "$p0 send"
$ns at 1.0 "$p1 send"
$ns at 1.2 "$cbr0 stop"
$ns at 1.4 "$p0 send"
$ns at 1.6 "$p1 send"
$ns at 1.8 "Finish"
$ns run


