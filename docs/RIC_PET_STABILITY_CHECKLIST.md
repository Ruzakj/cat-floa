# RIC Pet stability gate

Use this checklist after every behavior, overlay, lifecycle, or UI change. Keep each change small and verify the build before starting the next one.

## Build gate
- Debug APK compiles successfully in GitHub Actions.
- No new Android manifest, Java compiler, or resource errors.
- Do not stack another functional change while CI is red or pending.

## Floating pet regression gate
- Pet appears after overlay permission is granted.
- Dragging remains responsive and clamps the pet inside the visible display.
- Single tap and double tap still trigger the expected reaction and sound.
- Walk/run animation remains smooth and does not jump after changing direction.
- Portrait/landscape changes keep the pet on-screen.
- Screen lock/unlock restores a visible, interactive pet when Android permits overlays.
- Starting the service repeatedly does not create duplicate overlays.
- Stopping/restarting the service does not leave stale callbacks or duplicate behavior loops.

## Media and lifecycle gate
- Music/media detection changes state without interrupting dragging.
- Headphone state selects the intended media reaction.
- Closing the control activity does not stop the foreground pet unexpectedly.
- Service destruction removes overlay resources and scheduled callbacks cleanly.

## UI/UX Pro Max gate
- Interactive controls have at least a 48dp practical touch target.
- Controls have meaningful accessibility descriptions/state announcements where needed.
- Text and system-bar icons maintain readable contrast.
- Primary actions are visually distinguishable from secondary controls.
- UI changes never alter sprite artwork, animation timing, or pet behavior unless that iteration explicitly targets those systems.

## Release gate
A change is considered verified only after its own GitHub Actions run is green. Device-level items above remain manual QA requirements unless an instrumentation/device test is added for them.
