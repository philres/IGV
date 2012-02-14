package org.broad.igv.track;

import org.broad.igv.exceptions.DataLoadException;
import org.broad.igv.feature.FeatureDB;
import org.broad.igv.feature.genome.Genome;
import org.broad.igv.util.ResourceLocator;
import org.broad.igv.util.TestUtils;
import org.broad.tribble.Feature;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.*;


/**
 * @author Jim Robinson
 * @date 10/3/11
 */
public class TrackLoaderTest {

    TrackLoader trackLoader;

    @Before
    public void setUp() throws Exception {
        TestUtils.setUpHeadless();
        trackLoader = new TrackLoader();
    }

    @Test
    public void testLoadBEDIndexed() throws Exception {
        String filepath = TestUtils.DATA_DIR + "/bed/intervalTest.bed";
        TestUtils.createIndex(filepath);
        tstLoadFi(filepath, 1);

    }

    @Test
    public void testLoadBEDNotIndexed() throws Exception {
        String filepath = TestUtils.DATA_DIR + "/bed/intervalTest.bed";
        if (TrackLoader.isIndexed(filepath)) {
            File f = new File(filepath + ".idx");
            f.delete();
        }
        tstLoadFi(filepath, 1);

    }


    @Test
    public void testBEDCodec1() throws Exception {
        String filepath = TestUtils.DATA_DIR + "/bed/NA12878.deletions.10kbp.het.gq99.hand_curated.hg19.bed";
        boolean found_ex = false;
        try {
            tstLoadFi(filepath, null);
        } catch (DataLoadException ex) {
            found_ex = true;
        }
        assertTrue(found_ex);
    }

    private List<Track> tstLoadFi(String filepath, Integer expected_tracks) throws Exception {
        Genome genome = TestUtils.loadGenome();
        return tstLoadFi(filepath, expected_tracks, genome);
    }

    private List<Track> tstLoadFi(String filepath, Integer expected_tracks, Genome genome) throws Exception {
        ResourceLocator locator = new ResourceLocator(filepath);

        List<Track> tracks = trackLoader.load(locator, genome);
        if (expected_tracks != null) {
            assertEquals(expected_tracks.intValue(), tracks.size());
        }
        Track track = tracks.get(0);
        assertEquals(locator, track.getResourceLocator());

        return tracks;
    }

    @Test
    public void testBEDLoadsAliases() throws Exception {
        tstLoadFi(TestUtils.DATA_DIR + "/bed/canFam2_alias.bed", 1);
        String[] aliases = new String[]{"AAAA", "BBB", "CCC"};
        for (String alias : aliases) {
            Feature feat = FeatureDB.getFeature(alias);
            assertNotNull(feat);
        }

    }

    private static String[] filenames = new String[]{"/bb/chr21.refseq.bb", "/bed/MT_test.bed", "/bed/Unigene.sample.bed",
            "/bed/test.bed", "/cn/HindForGISTIC.hg16.cn", "/folder with spaces/test.wig",
            "/gct/igv_test2.gct", "/gct/affy_human_mod.gct", "/gff/gene.gff3", "/igv/MIP_44.cn",//"/gc/chr1.txt",
            "/maf/TCGA_GBM_Level3_Somatic_Mutations_08.28.2008.maf.gz", "/psl/fishBlat.psl", "/sam/test_2.sam",
            "/seg/canFam2_hg18.seg", "/wig/test.wig"};

    @Test
    public void testFilesHeadless() throws Exception {
        Genome genome = TestUtils.loadGenome();
        for (String finame : filenames) {
            tstLoadFi(TestUtils.DATA_DIR + finame, null, genome);
        }
    }

    @Test
    public void testFilesHeaded() throws Exception {
        TestUtils.startGUI();

        String ex_filename = "/vcf/example4-last-gsnap-2_fixed.vcf";
        Genome genome = TestUtils.loadGenome();
        List<String> finames = new ArrayList<String>(Arrays.asList(filenames));

        finames.add(ex_filename);

        for (String finame : finames) {
            tstLoadFi(TestUtils.DATA_DIR + finame, null, genome);
        }
        TestUtils.stopGUI();
    }


}
