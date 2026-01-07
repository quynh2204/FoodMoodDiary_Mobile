#!/bin/bash
# Set emulator GPS location to Hanoi, Vietnam
# Usage: ./set_emulator_location.sh

echo "Setting emulator GPS location to Hanoi, Vietnam..."
echo "Latitude: 21.0285, Longitude: 105.8542"

# Send GPS location via telnet to emulator
# Hanoi coordinates: 21.0285° N, 105.8542° E
adb emu geo fix 105.8542 21.0285

echo "✓ Location set successfully!"
echo ""
echo "To set different locations:"
echo "  Ho Chi Minh: adb emu geo fix 106.6297 10.8231"
echo "  Da Nang: adb emu geo fix 108.2022 16.0544"
echo "  Hanoi: adb emu geo fix 105.8542 21.0285"
