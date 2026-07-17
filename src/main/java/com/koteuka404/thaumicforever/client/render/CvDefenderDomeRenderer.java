package com.koteuka404.thaumicforever.client.render;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import com.koteuka404.thaumicforever.tile.TileCvDefender;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thaumcraft.api.aspects.Aspect;


public final class CvDefenderDomeRenderer {


        private static final int GEODESIC_FREQUENCY = 3;


        private static final double PANEL_INSET = 1.0D;

    private static final float SHELL_ALPHA = 0.045F;
    private static final float PANEL_ALPHA = 0.105F;
    private static final float LINE_ALPHA = 0.82F;


    private static final boolean RENDER_ASPECT_GLYPHS = true;
    private static final float ASPECT_GLYPH_ALPHA = 0.52F;
    private static final double ASPECT_GLYPH_RADIUS_OFFSET = 0.070D;
    private static final double ASPECT_GLYPH_SIZE_FACTOR = 0.065D;
    private static final double MIN_ASPECT_GLYPH_HALF_SIZE = 0.28D;

    private static final boolean RENDER_DEFENDER_BEAM = false;
    private static final double DEFENDER_BEAM_START_Y = 11.2D / 16.0D;
    private static final double DEFENDER_BEAM_TOP_OFFSET = 0.06D;
    private static final double DEFENDER_BEAM_MIN_HALF_WIDTH = 0.055D;
    private static final double DEFENDER_BEAM_MAX_HALF_WIDTH = 0.125D;
    private static final float DEFENDER_BEAM_OUTER_ALPHA = 0.25F;
    private static final float DEFENDER_BEAM_CORE_ALPHA = 0.78F;

    private static final GeodesicMesh GEODESIC_MESH =
            GeodesicMesh.create(GEODESIC_FREQUENCY);

    private CvDefenderDomeRenderer() {
    }

    @SubscribeEvent
    public static void render(RenderWorldLastEvent event) {
        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft.world == null || minecraft.player == null) {
            return;
        }

        GlStateManager.pushMatrix();
        GlStateManager.translate(
                -minecraft.getRenderManager().viewerPosX,
                -minecraft.getRenderManager().viewerPosY,
                -minecraft.getRenderManager().viewerPosZ
        );

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.enableDepth();
        GlStateManager.depthMask(false);

        GL11.glLineWidth(1.5F);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        for (TileEntity tile : minecraft.world.loadedTileEntityList) {
            if (!(tile instanceof TileCvDefender)) {
                continue;
            }

            TileCvDefender defender = (TileCvDefender) tile;
            if (!defender.isActivating() && !defender.isShieldActive()) {
                continue;
            }

            renderGeodesicShield(tessellator, buffer, defender);
        }

        GL11.glLineWidth(1.0F);

        GlStateManager.depthMask(true);
        GlStateManager.enableCull();
        GlStateManager.enableLighting();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    private static void renderGeodesicShield(Tessellator tessellator,
                                            BufferBuilder buffer,
                                            TileCvDefender defender) {
        double radius = defender.getDomeRadius();
        if (radius <= 0.0D) {
            return;
        }

        double centerX = defender.getPos().getX() + 0.5D;
        double centerY = defender.getPos().getY() + 0.5D;
        double centerZ = defender.getPos().getZ() + 0.5D;

        int color = Aspect.PROTECT.getColor();
        float red = ((color >> 16) & 255) / 255.0F;
        float green = ((color >> 8) & 255) / 255.0F;
        float blue = (color & 255) / 255.0F;

        renderBaseShell(
                tessellator,
                buffer,
                centerX,
                centerY,
                centerZ,
                radius,
                red,
                green,
                blue
        );


        double fillRadius = radius + 0.020D;
        buffer.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_COLOR);

        for (GeodesicCell cell : GEODESIC_MESH.cells) {
            appendCellFill(
                    buffer,
                    cell,
                    centerX,
                    centerY,
                    centerZ,
                    fillRadius,
                    red,
                    green,
                    blue
            );
        }

        tessellator.draw();

        double lineRadius = radius + 0.038D;
        buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);

        for (GeodesicCell cell : GEODESIC_MESH.cells) {
            appendCellLines(
                    buffer,
                    cell,
                    centerX,
                    centerY,
                    centerZ,
                    lineRadius,
                    red,
                    green,
                    blue
            );
        }

        tessellator.draw();

        if (RENDER_ASPECT_GLYPHS) {
                renderAspectGlyphs(
                tessellator,
                buffer,
                centerX,
                centerY,
                centerZ,
                radius
                );
        }

        if (RENDER_DEFENDER_BEAM && defender.isShieldActive()) {
                renderDefenderBeam(
                        tessellator,
                        buffer,
                        defender,
                        centerX,
                        centerZ,
                        radius,
                        red,
                        green,
                        blue
                );
        }
        }

    private static void renderDefenderBeam(Tessellator tessellator,
                                        BufferBuilder buffer,
                                        TileCvDefender defender,
                                        double centerX,
                                        double centerZ,
                                        double radius,
                                        float red,
                                        float green,
                                        float blue) {
        double bottomY = defender.getPos().getY()
                + DEFENDER_BEAM_START_Y;
        double topY = defender.getPos().getY()
                + 0.5D
                + radius
                - DEFENDER_BEAM_TOP_OFFSET;

        if (topY <= bottomY) {
            return;
        }

        Minecraft minecraft = Minecraft.getMinecraft();
        double renderTime = defender.getWorld().getTotalWorldTime()
                + minecraft.getRenderPartialTicks();

        double pulse = 0.82D
                + Math.sin(renderTime * 0.28D) * 0.18D;

        double halfWidth = Math.max(
                DEFENDER_BEAM_MIN_HALF_WIDTH,
                Math.min(
                        DEFENDER_BEAM_MAX_HALF_WIDTH,
                        radius * 0.011D
                )
        ) * pulse;

        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(
                GL11.GL_SRC_ALPHA,
                GL11.GL_ONE
        );

        buffer.begin(
                GL11.GL_QUADS,
                DefaultVertexFormats.POSITION_COLOR
        );

        for (int plane = 0; plane < 3; plane++) {
            double angle = plane * Math.PI / 3.0D;

            appendVerticalBeamPlane(
                    buffer,
                    centerX,
                    bottomY,
                    topY,
                    centerZ,
                    halfWidth,
                    angle,
                    red,
                    green,
                    blue,
                    DEFENDER_BEAM_OUTER_ALPHA
            );

            appendVerticalBeamPlane(
                    buffer,
                    centerX,
                    bottomY,
                    topY,
                    centerZ,
                    halfWidth * 0.34D,
                    angle,
                    1.0F,
                    1.0F,
                    1.0F,
                    DEFENDER_BEAM_CORE_ALPHA
            );
        }


        double flareBottom = Math.max(
                bottomY,
                topY - halfWidth * 4.0D
        );

        for (int plane = 0; plane < 3; plane++) {
            appendVerticalBeamPlane(
                    buffer,
                    centerX,
                    flareBottom,
                    topY,
                    centerZ,
                    halfWidth * 2.4D,
                    plane * Math.PI / 3.0D,
                    red,
                    green,
                    blue,
                    DEFENDER_BEAM_OUTER_ALPHA * 0.75F
            );
        }

        tessellator.draw();

        GlStateManager.blendFunc(
                GL11.GL_SRC_ALPHA,
                GL11.GL_ONE_MINUS_SRC_ALPHA
        );
    }

    private static void appendVerticalBeamPlane(
            BufferBuilder buffer,
            double centerX,
            double bottomY,
            double topY,
            double centerZ,
            double halfWidth,
            double angle,
            float red,
            float green,
            float blue,
            float alpha) {
        double offsetX = Math.cos(angle) * halfWidth;
        double offsetZ = Math.sin(angle) * halfWidth;

        buffer.pos(
                centerX - offsetX,
                bottomY,
                centerZ - offsetZ
        ).color(red, green, blue, alpha).endVertex();

        buffer.pos(
                centerX - offsetX,
                topY,
                centerZ - offsetZ
        ).color(red, green, blue, alpha).endVertex();

        buffer.pos(
                centerX + offsetX,
                topY,
                centerZ + offsetZ
        ).color(red, green, blue, alpha).endVertex();

        buffer.pos(
                centerX + offsetX,
                bottomY,
                centerZ + offsetZ
        ).color(red, green, blue, alpha).endVertex();
    }

    private static void renderAspectGlyphs(Tessellator tessellator,
                                        BufferBuilder buffer,
                                        double centerX,
                                        double centerY,
                                        double centerZ,
                                        double radius) {
        Minecraft minecraft = Minecraft.getMinecraft();
        if (Aspect.PROTECT.getImage() == null) {
            return;
        }

        minecraft.getTextureManager().bindTexture(
                Aspect.PROTECT.getImage()
        );

        int aspectColor = Aspect.PROTECT.getColor();
        float glyphRed = ((aspectColor >> 16) & 255) / 255.0F;
        float glyphGreen = ((aspectColor >> 8) & 255) / 255.0F;
        float glyphBlue = (aspectColor & 255) / 255.0F;

        GlStateManager.enableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        double glyphRadius = radius + ASPECT_GLYPH_RADIUS_OFFSET;
        double halfSize = Math.max(
                MIN_ASPECT_GLYPH_HALF_SIZE,
                radius * ASPECT_GLYPH_SIZE_FACTOR
        );

        buffer.begin(
                GL11.GL_QUADS,
                DefaultVertexFormats.POSITION_TEX_COLOR
        );

        for (GeodesicCell cell : GEODESIC_MESH.cells) {
            /*
             * Only true hexagons receive a glyph. The twelve pentagons remain
             * clean and act as visual anchor points on the sphere.
             */
            if (cell.corners.size() != 6) {
                continue;
            }

            appendAspectGlyph(
                    buffer,
                    cell.center,
                    centerX,
                    centerY,
                    centerZ,
                    glyphRadius,
                    halfSize,
                    glyphRed,
                    glyphGreen,
                    glyphBlue
            );
        }

        tessellator.draw();

        /*
         * The rest of the dome renderer uses untextured geometry.
         */
        GlStateManager.disableTexture2D();
    }

    private static void appendAspectGlyph(BufferBuilder buffer,
                                        Vec3d normal,
                                        double centerX,
                                        double centerY,
                                        double centerZ,
                                        double radius,
                                        double halfSize,
                                        float red,
                                        float green,
                                        float blue) {
        Vec3d reference = Math.abs(normal.y) < 0.90D
                ? new Vec3d(0.0D, 1.0D, 0.0D)
                : new Vec3d(1.0D, 0.0D, 0.0D);

        Vec3d tangentX = normalize(
                reference.crossProduct(normal)
        );
        Vec3d tangentY = normalize(
                normal.crossProduct(tangentX)
        );

        Vec3d surfaceCenter = normal.scale(radius);

        Vec3d bottomLeft = surfaceCenter
                .subtract(tangentX.scale(halfSize))
                .subtract(tangentY.scale(halfSize));
        Vec3d bottomRight = surfaceCenter
                .add(tangentX.scale(halfSize))
                .subtract(tangentY.scale(halfSize));
        Vec3d topRight = surfaceCenter
                .add(tangentX.scale(halfSize))
                .add(tangentY.scale(halfSize));
        Vec3d topLeft = surfaceCenter
                .subtract(tangentX.scale(halfSize))
                .add(tangentY.scale(halfSize));

        addGlyphVertex(
                buffer,
                bottomLeft,
                centerX,
                centerY,
                centerZ,
                0.0D,
                1.0D,
                red,
                green,
                blue
        );
        addGlyphVertex(
                buffer,
                bottomRight,
                centerX,
                centerY,
                centerZ,
                1.0D,
                1.0D,
                red,
                green,
                blue
        );
        addGlyphVertex(
                buffer,
                topRight,
                centerX,
                centerY,
                centerZ,
                1.0D,
                0.0D,
                red,
                green,
                blue
        );
        addGlyphVertex(
                buffer,
                topLeft,
                centerX,
                centerY,
                centerZ,
                0.0D,
                0.0D,
                red,
                green,
                blue
        );
    }

    private static void addGlyphVertex(BufferBuilder buffer,
                                        Vec3d position,
                                        double centerX,
                                        double centerY,
                                        double centerZ,
                                        double textureU,
                                        double textureV,
                                        float red,
                                        float green,
                                        float blue) {
        buffer.pos(
                centerX + position.x,
                centerY + position.y,
                centerZ + position.z
        ).tex(
                textureU,
                textureV
        ).color(
                red,
                green,
                blue,
                ASPECT_GLYPH_ALPHA
        ).endVertex();
    }

    private static void appendCellFill(BufferBuilder buffer,
                                        GeodesicCell cell,
                                        double centerX,
                                        double centerY,
                                        double centerZ,
                                        double radius,
                                        float red,
                                        float green,
                                        float blue) {
        int cornerCount = cell.corners.size();
        if (cornerCount < 3) {
            return;
        }

        for (int index = 0; index < cornerCount; index++) {
            Vec3d cornerA = insetOnSphere(
                    cell.center,
                    cell.corners.get(index),
                    PANEL_INSET
            );
            Vec3d cornerB = insetOnSphere(
                    cell.center,
                    cell.corners.get((index + 1) % cornerCount),
                    PANEL_INSET
            );

            addVertex(
                    buffer,
                    cell.center,
                    centerX,
                    centerY,
                    centerZ,
                    radius,
                    red,
                    green,
                    blue,
                    PANEL_ALPHA
            );
            addVertex(
                    buffer,
                    cornerA,
                    centerX,
                    centerY,
                    centerZ,
                    radius,
                    red,
                    green,
                    blue,
                    PANEL_ALPHA
            );
            addVertex(
                    buffer,
                    cornerB,
                    centerX,
                    centerY,
                    centerZ,
                    radius,
                    red,
                    green,
                    blue,
                    PANEL_ALPHA
            );
        }
    }

    private static void appendCellLines(BufferBuilder buffer,
                                        GeodesicCell cell,
                                        double centerX,
                                        double centerY,
                                        double centerZ,
                                        double radius,
                                        float red,
                                        float green,
                                        float blue) {
        int cornerCount = cell.corners.size();
        if (cornerCount < 3) {
            return;
        }

        for (int index = 0; index < cornerCount; index++) {
            Vec3d cornerA = insetOnSphere(
                    cell.center,
                    cell.corners.get(index),
                    PANEL_INSET
            );
            Vec3d cornerB = insetOnSphere(
                    cell.center,
                    cell.corners.get((index + 1) % cornerCount),
                    PANEL_INSET
            );

            addVertex(
                    buffer,
                    cornerA,
                    centerX,
                    centerY,
                    centerZ,
                    radius,
                    red,
                    green,
                    blue,
                    LINE_ALPHA
            );
            addVertex(
                    buffer,
                    cornerB,
                    centerX,
                    centerY,
                    centerZ,
                    radius,
                    red,
                    green,
                    blue,
                    LINE_ALPHA
            );
        }
    }

    private static Vec3d insetOnSphere(Vec3d center,
                                        Vec3d corner,
                                        double amount) {
        Vec3d blended = center.scale(1.0D - amount)
                .add(corner.scale(amount));
        return normalize(blended);
    }

    private static void addVertex(BufferBuilder buffer,
                                Vec3d unitPosition,
                                double centerX,
                                double centerY,
                                double centerZ,
                                double radius,
                                float red,
                                float green,
                                float blue,
                                float alpha) {
        buffer.pos(
                centerX + unitPosition.x * radius,
                centerY + unitPosition.y * radius,
                centerZ + unitPosition.z * radius
        ).color(red, green, blue, alpha).endVertex();
    }

    private static void renderBaseShell(Tessellator tessellator,
                                        BufferBuilder buffer,
                                        double centerX,
                                        double centerY,
                                        double centerZ,
                                        double radius,
                                        float red,
                                        float green,
                                        float blue) {
        final int rings = 16;
        final int segments = 32;

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

        for (int ring = 0; ring < rings; ring++) {
            double phi0 = Math.PI * ring / rings;
            double phi1 = Math.PI * (ring + 1) / rings;

            for (int segment = 0; segment < segments; segment++) {
                double theta0 = Math.PI * 2.0D * segment / segments;
                double theta1 = Math.PI * 2.0D * (segment + 1) / segments;

                addSpherePoint(
                        buffer,
                        centerX,
                        centerY,
                        centerZ,
                        radius,
                        phi0,
                        theta0,
                        red,
                        green,
                        blue,
                        SHELL_ALPHA
                );
                addSpherePoint(
                        buffer,
                        centerX,
                        centerY,
                        centerZ,
                        radius,
                        phi0,
                        theta1,
                        red,
                        green,
                        blue,
                        SHELL_ALPHA
                );
                addSpherePoint(
                        buffer,
                        centerX,
                        centerY,
                        centerZ,
                        radius,
                        phi1,
                        theta1,
                        red,
                        green,
                        blue,
                        SHELL_ALPHA
                );
                addSpherePoint(
                        buffer,
                        centerX,
                        centerY,
                        centerZ,
                        radius,
                        phi1,
                        theta0,
                        red,
                        green,
                        blue,
                        SHELL_ALPHA
                );
            }
        }

        tessellator.draw();
    }

    private static void addSpherePoint(BufferBuilder buffer,
                                        double centerX,
                                        double centerY,
                                        double centerZ,
                                        double radius,
                                        double phi,
                                        double theta,
                                        float red,
                                        float green,
                                        float blue,
                                        float alpha) {
        double sinPhi = Math.sin(phi);

        buffer.pos(
                centerX + radius * sinPhi * Math.cos(theta),
                centerY + radius * Math.cos(phi),
                centerZ + radius * sinPhi * Math.sin(theta)
        ).color(red, green, blue, alpha).endVertex();
    }

    private static Vec3d normalize(Vec3d vector) {
        double lengthSquared = vector.x * vector.x
                + vector.y * vector.y
                + vector.z * vector.z;

        if (lengthSquared <= 1.0E-18D) {
            return new Vec3d(0.0D, 1.0D, 0.0D);
        }

        double inverseLength = 1.0D / Math.sqrt(lengthSquared);
        return new Vec3d(
                vector.x * inverseLength,
                vector.y * inverseLength,
                vector.z * inverseLength
        );
    }

    private static final class GeodesicMesh {
        private final List<GeodesicCell> cells;

        private GeodesicMesh(List<GeodesicCell> cells) {
            this.cells = Collections.unmodifiableList(cells);
        }

        private static GeodesicMesh create(int frequency) {
            if (frequency < 1) {
                throw new IllegalArgumentException(
                        "Geodesic frequency must be at least 1"
                );
            }

            double goldenRatio = (1.0D + Math.sqrt(5.0D)) * 0.5D;

            Vec3d[] baseVertices = {
                    new Vec3d(-1.0D, goldenRatio, 0.0D),
                    new Vec3d(1.0D, goldenRatio, 0.0D),
                    new Vec3d(-1.0D, -goldenRatio, 0.0D),
                    new Vec3d(1.0D, -goldenRatio, 0.0D),

                    new Vec3d(0.0D, -1.0D, goldenRatio),
                    new Vec3d(0.0D, 1.0D, goldenRatio),
                    new Vec3d(0.0D, -1.0D, -goldenRatio),
                    new Vec3d(0.0D, 1.0D, -goldenRatio),

                    new Vec3d(goldenRatio, 0.0D, -1.0D),
                    new Vec3d(goldenRatio, 0.0D, 1.0D),
                    new Vec3d(-goldenRatio, 0.0D, -1.0D),
                    new Vec3d(-goldenRatio, 0.0D, 1.0D)
            };

            for (int index = 0; index < baseVertices.length; index++) {
                baseVertices[index] = normalize(baseVertices[index]);
            }

            int[][] baseFaces = {
                    {0, 11, 5},
                    {0, 5, 1},
                    {0, 1, 7},
                    {0, 7, 10},
                    {0, 10, 11},

                    {1, 5, 9},
                    {5, 11, 4},
                    {11, 10, 2},
                    {10, 7, 6},
                    {7, 1, 8},

                    {3, 9, 4},
                    {3, 4, 2},
                    {3, 2, 6},
                    {3, 6, 8},
                    {3, 8, 9},

                    {4, 9, 5},
                    {2, 4, 11},
                    {6, 2, 10},
                    {8, 6, 7},
                    {9, 8, 1}
            };

            List<Vec3d> vertices = new ArrayList<Vec3d>();
            List<Triangle> triangles = new ArrayList<Triangle>();
            Map<VertexKey, Integer> vertexIndices =
                    new HashMap<VertexKey, Integer>();

            for (int[] face : baseFaces) {
                subdivideFace(
                        baseVertices[face[0]],
                        baseVertices[face[1]],
                        baseVertices[face[2]],
                        frequency,
                        vertices,
                        triangles,
                        vertexIndices
                );
            }

            List<List<Integer>> incidentTriangles =
                    new ArrayList<List<Integer>>(vertices.size());

            for (int vertexIndex = 0;
                vertexIndex < vertices.size();
                vertexIndex++) {
                incidentTriangles.add(new ArrayList<Integer>(6));
            }

            for (int triangleIndex = 0;
                triangleIndex < triangles.size();
                triangleIndex++) {
                Triangle triangle = triangles.get(triangleIndex);
                incidentTriangles.get(triangle.a).add(triangleIndex);
                incidentTriangles.get(triangle.b).add(triangleIndex);
                incidentTriangles.get(triangle.c).add(triangleIndex);
            }

            List<Vec3d> triangleCenters =
                    new ArrayList<Vec3d>(triangles.size());

            for (Triangle triangle : triangles) {
                Vec3d a = vertices.get(triangle.a);
                Vec3d b = vertices.get(triangle.b);
                Vec3d c = vertices.get(triangle.c);

                triangleCenters.add(normalize(
                        new Vec3d(
                                a.x + b.x + c.x,
                                a.y + b.y + c.y,
                                a.z + b.z + c.z
                        )
                ));
            }

            List<GeodesicCell> cells =
                    new ArrayList<GeodesicCell>(vertices.size());

            for (int vertexIndex = 0;
                vertexIndex < vertices.size();
                vertexIndex++) {
                Vec3d center = vertices.get(vertexIndex);
                List<Integer> incident = incidentTriangles.get(vertexIndex);

                /*
                 * A valid dual of a subdivided icosahedron contains only
                 * pentagons and hexagons.
                 */
                if (incident.size() != 5 && incident.size() != 6) {
                    continue;
                }

                List<AngularCorner> angularCorners =
                        new ArrayList<AngularCorner>(incident.size());

                Vec3d reference = Math.abs(center.y) < 0.9D
                        ? new Vec3d(0.0D, 1.0D, 0.0D)
                        : new Vec3d(1.0D, 0.0D, 0.0D);

                Vec3d tangentX = normalize(reference.crossProduct(center));
                Vec3d tangentY = normalize(center.crossProduct(tangentX));

                for (Integer triangleIndex : incident) {
                    Vec3d corner = triangleCenters.get(triangleIndex.intValue());

                    double normalComponent = corner.dotProduct(center);
                    Vec3d projected = corner.subtract(
                            center.scale(normalComponent)
                    );

                    double angle = Math.atan2(
                            projected.dotProduct(tangentY),
                            projected.dotProduct(tangentX)
                    );

                    angularCorners.add(new AngularCorner(angle, corner));
                }

                Collections.sort(
                        angularCorners,
                        new Comparator<AngularCorner>() {
                            @Override
                            public int compare(AngularCorner first,
                                                AngularCorner second) {
                                return Double.compare(
                                        first.angle,
                                        second.angle
                                );
                            }
                        }
                );

                List<Vec3d> orderedCorners =
                        new ArrayList<Vec3d>(angularCorners.size());

                for (AngularCorner angularCorner : angularCorners) {
                    orderedCorners.add(angularCorner.position);
                }

                cells.add(new GeodesicCell(
                        center,
                        Collections.unmodifiableList(orderedCorners)
                ));
            }

            return new GeodesicMesh(cells);
        }

        private static void subdivideFace(Vec3d vertexA,
                                        Vec3d vertexB,
                                        Vec3d vertexC,
                                        int frequency,
                                        List<Vec3d> vertices,
                                        List<Triangle> triangles,
                                        Map<VertexKey, Integer> vertexIndices) {
            int[][] grid = new int[frequency + 1][];

            for (int row = 0; row <= frequency; row++) {
                grid[row] = new int[frequency - row + 1];

                for (int column = 0;
                    column <= frequency - row;
                    column++) {
                    double weightB = row / (double) frequency;
                    double weightC = column / (double) frequency;
                    double weightA = 1.0D - weightB - weightC;

                    Vec3d position = normalize(new Vec3d(
                            vertexA.x * weightA
                                    + vertexB.x * weightB
                                    + vertexC.x * weightC,
                            vertexA.y * weightA
                                    + vertexB.y * weightB
                                    + vertexC.y * weightC,
                            vertexA.z * weightA
                                    + vertexB.z * weightB
                                    + vertexC.z * weightC
                    ));

                    grid[row][column] = getOrCreateVertex(
                            position,
                            vertices,
                            vertexIndices
                    );
                }
            }

            for (int row = 0; row < frequency; row++) {
                for (int column = 0;
                    column < frequency - row;
                    column++) {
                    int top = grid[row][column];
                    int lower = grid[row + 1][column];
                    int right = grid[row][column + 1];

                    triangles.add(new Triangle(top, lower, right));

                    if (column < frequency - row - 1) {
                        int lowerRight = grid[row + 1][column + 1];
                        triangles.add(new Triangle(
                                lower,
                                lowerRight,
                                right
                        ));
                    }
                }
            }
        }

        private static int getOrCreateVertex(
                Vec3d position,
                List<Vec3d> vertices,
                Map<VertexKey, Integer> vertexIndices) {
            VertexKey key = new VertexKey(position);
            Integer existing = vertexIndices.get(key);

            if (existing != null) {
                return existing.intValue();
            }

            int newIndex = vertices.size();
            vertices.add(position);
            vertexIndices.put(key, Integer.valueOf(newIndex));
            return newIndex;
        }
    }

    private static final class GeodesicCell {
        private final Vec3d center;
        private final List<Vec3d> corners;

        private GeodesicCell(Vec3d center, List<Vec3d> corners) {
            this.center = center;
            this.corners = corners;
        }
    }

    private static final class Triangle {
        private final int a;
        private final int b;
        private final int c;

        private Triangle(int a, int b, int c) {
            this.a = a;
            this.b = b;
            this.c = c;
        }
    }

    private static final class AngularCorner {
        private final double angle;
        private final Vec3d position;

        private AngularCorner(double angle, Vec3d position) {
            this.angle = angle;
            this.position = position;
        }
    }


    private static final class VertexKey {
        private static final double PRECISION = 100000000.0D;

        private final long x;
        private final long y;
        private final long z;

        private VertexKey(Vec3d position) {
            this.x = Math.round(position.x * PRECISION);
            this.y = Math.round(position.y * PRECISION);
            this.z = Math.round(position.z * PRECISION);
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (!(object instanceof VertexKey)) {
                return false;
            }

            VertexKey other = (VertexKey) object;
            return x == other.x && y == other.y && z == other.z;
        }

        @Override
        public int hashCode() {
            int result = Long.hashCode(x);
            result = 31 * result + Long.hashCode(y);
            result = 31 * result + Long.hashCode(z);
            return result;
        }
    }
}
