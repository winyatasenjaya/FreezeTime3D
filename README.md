# Freeze Time 3D

FreezeTime3D is a way for you and a bunch of your friends to easily create the "bullet time" effect from The Matrix that you know so well.

## What you'll need

- A computer running node.js that will act as the main controlling server. A Raspberry Pi is an ideal small, portable server. A pre-configured system image for the Pi will be added to this repo at a later date to make things easy.
- Any number of iOS and Android devices with rear-facing cameras. I'd recommend at least 24. Yes, you'll need a **bunch** of friends.

## How it works

The "bullet time" effect is achieved by lining up a bunch of still cameras - usually pointing at the same thing - and having them fire at or very close to the same time when some event takes place. By playing the images back in order at a regular framerate, you create the effect of moving the camera through 3d space while time is frozen.

Most people don't have access to multiple still/video cameras to create this setup, let alone figuring out a way to get them all to fire at the same time. But what I realized is that many people these days are carrying around smartphones with very good cameras on them. If a number of people could get together with their smartphones and have a way to get all the cameras to go off at the same time, you'd have the effect.

Hence, FreezeTime3D was born. It consists of two main programs: the node.js controlling server and a mobile app that can be run on iOS and Android. At a high level, this is what the components do:

- The server acts as a socket server for all of the mobile devices to coordinate the pic-taking, as well as providing a small website to see the status of everything going on.
- The mobile app is run on every smartphone and each device connects to the socket server. The actual pic-taking is controlled by one device that acts as the "Master," while all the other instances of the running mobile app act as "picture takers" in an ordered fashion.

## The name

"Bullet time" is a registered trademark of Warner Bros., so a different name was needed. FreezeTime3D seems to capture things well.
