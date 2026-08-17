# Replace launcher icons with adaptive icon set

This PR replaces the existing launcher icons with an adaptive icon set (foreground vector + background shape) to avoid AAPT2 resource compile failures caused by corrupted PNG files. It also includes placeholder PNGs for legacy density buckets so CI doesn't fail when mipmap PNGs are expected.

Files added/changed:
- app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml
- app/src/main/res/mipmap-anydpi-v26/ic_launcher_round.xml
- app/src/main/res/drawable/ic_launcher_foreground.xml
- app/src/main/res/drawable/ic_launcher_background.xml
- app/src/main/res/mipmap-*/ic_launcher.png (small placeholder PNGs)

Notes:
- Background color: #3DDC84
- Foreground: white task/checklist icon (vector)

Please review and merge.
