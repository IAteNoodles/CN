set ns [new Simulator]
# Set up ntrace and namfile
set ntrace [open 1.tr w]
$ns trace-all $ntrace
set namfile [open 1.nam w]
$ns namtrace-all $namfile
# Procedure
proc Finish {} {
	global ns ntrace namfile
	$ns flush-trace
	close $ntrace
	close $namfile

	exec nam 1.nam &
	puts "Dropped packets:" 
	exec grep -c "^d" 1.tr &
	exit 0
}
#Create 3 nodes
set n0 [$ns node]
set n1 [$ns node]
set n2 [$ns node]


# Set label and color
$n0 label "TCP Source"
$n2 label "Sink"
$ns color 1 blue

# Create a link

$ns duplex-link $n0 $n1 1Mb 10ms DropTail
$ns duplex-link $n1 $n2 1Mb 10ms DropTail

#Link Orientation
$ns duplex-link-op $n0 $n1 orient right
$ns duplex-link-op $n1 $n2 orient right

#Set Queue size

$ns queue-limit $n0 $n1 10
$ns queue-limit $n1 $n2 10

#  Transport layer

set tcp0 [new Agent/TCP]
$ns attach-agent $n0 $tcp0

set sink0 [new Agent/TCPSink]
$ns attach-agent $n2 $sink0

# Connect the two agents
$ns connect $tcp0 $sink0

#Application layer
set cbr0 [new Application/Traffic/CBR]
$cbr0 set type_ CBR
$cbr0 set packetSize_ 100
$cbr0 set random_ false
$cbr0 set rate_ 1Mb
$cbr0 attach-agent $tcp0
$tcp0 set class_ 1
$ns at 0.0 "$cbr0 start"
$ns at 5.0 "Finish"
$ns run

