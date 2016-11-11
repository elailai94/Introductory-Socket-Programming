#!/bin/bash

##==============================================================================
## Introductory Socket Programming
##
## @description: Run script for client.java
## @author: Elisha Lai
## @version: 1.0 11/10/2016
##==============================================================================

# Number of Parameters: 4
# Parameter:
#    $1: <server_address>
#    $2: <n_port>
#    $3: <req_code>
#    $4: <msg>
java client "$@"
