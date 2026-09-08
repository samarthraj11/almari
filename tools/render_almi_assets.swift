import AppKit

func pngData(size: CGFloat, transparent: Bool) -> Data {
    let image = NSImage(size: NSSize(width: size, height: size))
    image.lockFocus()

    if !transparent {
        NSColor(calibratedRed: 49 / 255, green: 87 / 255, blue: 246 / 255, alpha: 1).setFill()
        NSBezierPath(rect: NSRect(x: 0, y: 0, width: size, height: size)).fill()
    }

    let scale = size / 1024
    let closetRect = NSRect(x: 212 * scale, y: 190 * scale, width: 600 * scale, height: 600 * scale)
    let closet = NSBezierPath(roundedRect: closetRect, xRadius: 80 * scale, yRadius: 80 * scale)
    closet.lineWidth = 64 * scale
    NSColor.white.setStroke()
    closet.stroke()

    let details = NSBezierPath()
    details.move(to: NSPoint(x: 512 * scale, y: 190 * scale))
    details.line(to: NSPoint(x: 512 * scale, y: 790 * scale))
    details.move(to: NSPoint(x: 366 * scale, y: 610 * scale))
    details.line(to: NSPoint(x: 512 * scale, y: 484 * scale))
    details.line(to: NSPoint(x: 658 * scale, y: 610 * scale))
    details.lineWidth = 48 * scale
    details.lineCapStyle = .round
    details.lineJoinStyle = .round
    details.stroke()

    NSColor(calibratedRed: 200 / 255, green: 255 / 255, blue: 69 / 255, alpha: 1).setFill()
    NSBezierPath(ovalIn: NSRect(x: 490 * scale, y: 403 * scale, width: 44 * scale, height: 44 * scale)).fill()

    image.unlockFocus()
    let representation = NSBitmapImageRep(data: image.tiffRepresentation!)!
    if transparent {
        return representation.representation(using: .png, properties: [:])!
    }
    let jpeg = representation.representation(using: .jpeg, properties: [.compressionFactor: 1.0])!
    return NSBitmapImageRep(data: jpeg)!.representation(using: .png, properties: [:])!
}

let root = URL(fileURLWithPath: CommandLine.arguments[1])
try pngData(size: 1024, transparent: false).write(
    to: root.appendingPathComponent("iosApp/iosApp/Assets.xcassets/AppIcon.appiconset/almi-app-icon.png")
)
try pngData(size: 360, transparent: true).write(
    to: root.appendingPathComponent("iosApp/iosApp/Assets.xcassets/LaunchLogo.imageset/almi-launch-logo.png")
)
