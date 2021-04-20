Hello Linux Foundation.

The following is my master's thesis for my course in Advanced computer science at Kent university graduating in 2019.  A lot has changed since then with my coding abilities, as evidenced from my business website.

How will it help the Linux community:
It will avoid a problem which puts off newbies.  Installing new devices which are not supported by the OS.  It could leave new users of desktop systems free to not need  user the terminal for their day to day tasks.

This project is a proof of concept that Linux can use it's own version of the Windows device installer wizard.  I did write it in Java for the back end and C++ for the front end.  It automates the standard device install process which is currently:
apt install <package dependencies> (Debian based).
git clone <repo>
cd <repo-dir>
make
make install
modprobe -v <module name>

With a graphical means to install new device drivers across pci and usb.

How do developers submit a module?
Using oauth they share their public repositories via GitHub. The servers for this then ensure a makefile called linuxconf exists with the correct build instructions.

How do we know what's a successful install? 
We let the end users be the jury on that front, by allowing them to vote on the success of a device install.  The trending device installs then become promoted to the next user who downloads them.

This version is limited to working on Ubuntu Focal.  But we can progress it further from there in the following ways:

Adding udev rules so that the application works when a new device is plugged in.
Using cryptography to ensure device installs are verified.
Enabling it for all Linux package managers.
Using dkms.
Asking for user verification via email so that each user can only vote on the success of an install once per device.
Rewriting the server in Django and the client in Python.
Availability across all architectures.

One concern here is malware.  We're ultimately inserting code into the kernel as root.
We could have a team to vet each install, or create an area on stack overflow for new submissions with verification of each proposed install, with sign off available to chosen members.

What if it breaks /etc?
The system does a backup of /etc before each device install.

What if it breaks graphics drivers?
Wired into the startup of the user login which ran this program is a console based "revert to previous configuration" prompt.

Can I build this (well)?
I believe so.  I've referenced my business site on the submission to your page.  A lot has changed since I wrote this project, in 2019.  
It seems as if my choice of languages has gone from more complicated to simple.  At uni I learnt advanced Java and C++ before delving into python and php more recently.
Why? Because at the time I thought all languages were as hard to master as these and knew nothing different.

What are the shortcomings of what we have now?
It's vulnerable to SQL injections.  It also doesn't deal with connection issues. It is, albeit a bit flakey, proof that this can be done.
