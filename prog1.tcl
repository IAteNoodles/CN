# Create the simulator
set ns [new Simulator]

# Open trace and NAM files
set ntrace [open prog1.tr w]
$ns trace-all $ntrace
set namfile [open prog1.nam w]
$ns namtrace-all $namfile

# Finish Procedure
proc Finish {} {
    global ns ntrace namfile
    # Dump all the trace data and close the files
    $ns flush-trace
    close $ntrace
    close $namfile
    
    # Execute NAM animation file
    exec nam prog1.nam &
    
    # Show the number of packet drops in the trace file
    exec echo "The number of packet drops is:"
    exec grep -c "^d" prog1.tr
    
    # Exit simulation
    exit 0
}

# Create nodes
set n0 [$ns node]  ;# TCP source
set n1 [$ns node]  ;# Intermediate node
set n2 [$ns node]  ;# Sink

# Label nodes
$n0 label "TCP Source"
$n2 label "Sink"

# Set the color for visualization
$ns color 1 blue

# Create links between nodes (1Mbps, 10ms delay, DropTail queue)
$ns duplex-link $n0 $n1 1Mb 10ms DropTail
$ns duplex-link $n1 $n2 1Mb 10ms DropTail

# Set link orientation for visualization
$ns duplex-link-op $n0 $n1 orient right
$ns duplex-link-op $n1 $n2 orient right

# Set Queue size (Modify queue size to observe packet drop behavior)
$ns queue-limit $n0 $n1 10
$ns queue-limit $n1 $n2 10

# Set up a TCP connection (Transport layer)
set tcp0 [new Agent/TCP]
$ns attach-agent $n0 $tcp0

# Set up the TCP sink (Sink node)
set sink0 [new Agent/TCPSink]
$ns attach-agent $n2 $sink0
$ns connect $tcp0 $sink0

# Set up application traffic (CBR - Constant Bit Rate)
set cbr0 [new Application/Traffic/CBR]
$cbr0 set type_ CBR
$cbr0 set packetSize_ 100
$cbr0 set rate_ 1Mb
$cbr0 set random_ false
$cbr0 attach-agent $tcp0
$tcp0 set class_ 1

# Schedule events
$ns at 0.0 "$cbr0 start"   ;# Start sending CBR traffic
$ns at 5.0 "Finish"         ;# Call Finish procedure after 5 seconds

# Run the simulation
$ns run

