set ns [new Simulator]
$ns color 1 blue
$ns color 2 red
set ntrace [open prg2.tr w]
$ns trace-all $ntrace
set namfile [open prg2.nam w]
$ns namtrace-all $namfile
proc Finish {} {
global ns ntrace namfile
$ns flush-trace
close $ntrace
close $namfile
exec nam prg2.nam &
puts "Packets dropped:" 
exec grep "^d" | cut -d " " -f 5 | grep -c "ping" &
exit 0
}

for {set i 0} {$i<6} {incr i} {
set n($i) [$ns node]
}
for {set j 0} {$j<5} {incr j} {
$ns duplex-link $n($j) $n([expr ($j+1)]) 0.1Mb 10ms DropTail
}
Agent/Ping instproc recv {from rtt} {
$self instvar node_
puts "node [$node_ id] received ping from $from with round trip
time $rtt
ms"
}
set p0 [new Agent/Ping]
$p0 set class_ 1
$ns attach-agent $n(0) $p0
set p5 [new Agent/Ping]
$p5 set class_ 1
$ns attach-agent $n(5) $p5
$ns connect $p0 $p5
$ns queue-limit $n(2) $n(3) 2
$ns duplex-link-op $n(2) $n(3) queuePos 0.5
set tcp0 [new Agent/TCP]
$tcp0 set class_ 2
$ns attach-agent $n(2) $tcp0
set sink0 [new Agent/TCPSink]
$ns attach-agent $n(4) $sink0
$ns connect $tcp0 $sink0
set cbr0 [new Application/Traffic/CBR]
$cbr0 set packetSize_ 500
$cbr0 set rate_ 1Mb
$cbr0 attach-agent $tcp0
$ns at 0.2 "$p0 send"
$ns at 0.4 "$p5 send"
$ns at 0.4 "$cbr0 start"
$ns at 0.8 "$p0 send"
$ns at 1.0 "$p5 send"
$ns at 1.2 "$cbr0 stop"
$ns at 1.4 "$p0 send"
$ns at 1.6 "$p5 send"
$ns at 1.8 "Finish"

$ns run 
