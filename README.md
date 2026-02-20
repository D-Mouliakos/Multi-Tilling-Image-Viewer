# Multi-Tilling-Image-Viewer
A desktop image viewer designed for simultaneous viewing of multiple images in a dynamic tiled layout &amp; independent controls over each Image.
The application adapts in real time to window size, display resolution, ensuring consistent visual behavior across HD and 4K systems.

Built as a performance-conscious desktop UI project focused on layout correctness, responsiveness, and cross-environment stability.


## Key Features

- Dynamic multi-tile image grid

- Real-time layout adaptation during window resize

- Resolution-aware scaling across different displays

- Native system file dialog integration

- Lightweight runtime footprint

- Modular UI logic for maintainability

- Designed for predictable behavior across Windows environments


# Engineering Highlights
## Resolution-Adaptive Layout Engine

Layout scaling is derived from the actual runtime environment rather than fixed design assumptions.
This prevents visual drift across monitors, and window states.

## Container-Driven Scaling Model

UI behavior is based on usable content area instead of raw screen resolution.
This ensures consistency between normal and maximized window states.

## Performance-Oriented Rendering Approach

- Rendering workload scales only with active content

- Layout recalculation occurs only when necessary

- No unnecessary processing during idle UI states

## Platform-Aware UI Behavior

- The application accounts for real-world desktop constraints including:

- Window decorations and insets

- OS-native dialog behavior

## Technical Challenges

- Efficient Image Scaling via Caching to reducing CPU usage and improving UI responsiveness during window resizing.

- Preventing layout drift from window decoration differences

- Ensuring consistent rendering across HD and 4K displays

## 

## System Requirements

- Windows 10 or Windows 11 (Recomended)

- Java Runtime Environment (JRE), 64-bit

No development tools or JDK installation is required.

## Java Runtime Installation

To run the application, install a Java Runtime Environment.

Recommended download:

Java for Desktops (Windows x64)

https://www.java.com/en/download/

Download steps:

1. Open the website

2. Download Java for Desktops

3. Install normally

After installation, the application will run immediately.

## Running the Application
Run executable

1. Install Java Runtime

2. Launch: Muti Image Viewer.exe

## Technology Overview

- Desktop UI built with Java

- Native OS file dialog integration

- Resolution-adaptive layout system


## Project Purpose

- This project demonstrates practical desktop software engineering skills:

- Designing UI systems that behave correctly across environments

- Managing resolution variability

- Building responsive desktop interfaces

- Structuring UI logic for predictability and maintainability

- Handling platform-specific behavior in real-world conditions

# Why This Project Matters

Many desktop applications assume fixed display environments.
Multi Image Viewer is designed to behave consistently across:

- Different resolutions

- Different DPI scaling settings

- Different Windows versions

- Dynamic window resizing

This project reflects real-world desktop UI challenges rather than simplified examples.
