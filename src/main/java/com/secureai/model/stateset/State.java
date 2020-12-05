package com.secureai.model.stateset;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum State {

    // Model 1 VMs
    active(0),
    firewallBlockICMP(1),
    firewallSoftBandwidthLimit(2),
    firewallHeavyBandwidthLimit(3),
    appAvailable(4),
    restarted(5),
    corrupted(6),
    dockerRuncUpdated(7),
    dockerRuncUpgradable(8),
    dockerExecAvailable(9),
    containerCorrupted(10);

    //-------------------------------------------------------------------------------------
/*
    // Model 2 containers
    active(0),
    restarted(1),
    corrupted(2),
    shellCorrupted(3),
    cartCorrupted(4),
    confidentialityVulnerability(5),
    integrityVulnerability(6),
    passwordRequired(7),
    dangerousCmdEnabled(8),
    accessRestricted(9);
*/


    @Getter
    @NonNull
    private int value;

}
