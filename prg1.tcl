set ns [new Simulator]

set ntrace [open prg1.tr w]
$ns trace-all $ntrace

set namfile [open prg1.nam w]
$ns namtrace-all $namfile

proc Finish {} {
global ns ntrace namfile
$ns flush-trace
close $ntrace
close $namfile
exec nam prg1.nam &
exec echo "Dropped packets are:" &
exec grep -c "^d" prg1.tr &
exit 0
}

set n0 [$ns node]
set n1 [$ns node]
set n2 [$ns node]

$n0 label "Source"
$n2 label "TCPSink"

$ns duplex-link $n0 $n1 1Mb 10ms DropTail
$ns duplex-link $n1 $n2 1Mb 10ms DropTail

$ns duplex-link-op $n0 $n1 orient right
$ns duplex-link-op $n1 $n2 orient right

$ns color 1 blue

$ns queue-limit $n0 $n1 10
$ns queue-limit $n1 $n2 10

set tcp0 [new Agent/TCP]
$ns attach-agent $n0 $tcp0

set sink0 [new Agent/TCPSink]
$ns attach-agent $n2 $sink0

$ns connect $tcp0 $sink0

$tcp0 set class_ 1

set cbr0 [new Application/Traffic/CBR]
$cbr0 set type_ CBR
$cbr0 set packetSize_ 100
$cbr0 set rate_ 1Mb
$cbr0 set random_ false

$cbr0 attach-agent $tcp0

$ns at 0.0 "$cbr0 start"
$ns at 5.0 "Finish"
$ns run
