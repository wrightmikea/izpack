package com.izforge.izpack.compiler;

import com.izforge.izpack.api.data.binding.IzpackProjectInstaller;
import com.izforge.izpack.api.data.binding.Listener;
import com.izforge.izpack.api.data.binding.Stage;
import com.izforge.izpack.compiler.container.TestCompilerContainer;
import com.izforge.izpack.matcher.ObjectInputMatcher;
import com.izforge.izpack.matcher.ZipMatcher;
import com.izforge.izpack.test.Container;
import com.izforge.izpack.test.InstallFile;
import com.izforge.izpack.test.junit.PicoRunner;
import org.hamcrest.beans.HasPropertyWithValue;
import org.hamcrest.core.AllOf;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.junit.internal.matchers.IsCollectionContaining;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Test for custom data
 *
 * @author Anthonin Bonnefoy
 */
@RunWith(PicoRunner.class)
@Container(TestCompilerContainer.class)
public class CompilerConfigCustomDataTest
{
    private File out;
    private CompilerConfig compilerConfig;

    public CompilerConfigCustomDataTest(File out, CompilerConfig compilerConfig)
    {
        this.out = out;
        this.compilerConfig = compilerConfig;
    }

    @Test
    @InstallFile("samples/listeners.xml")
    public void testCustomDataArePresent() throws Exception
    {
        compilerConfig.executeCompiler();
        assertThat(out, ZipMatcher.isZipContainingFile("com/izforge/izpack/event/SummaryLoggerInstallerListener.class"));
        IzpackProjectInstaller izpackModel = (IzpackProjectInstaller) ObjectInputMatcher.getObjectFromZip(out, "resources/izpackInstallModel");
        List<Listener> listenerList = izpackModel.getListeners();
        assertThat(listenerList.size(), Is.is(3));
        assertThat(listenerList, IsCollectionContaining.hasItems(
                AllOf.allOf(
                        HasPropertyWithValue.<Listener>hasProperty("classname", Is.is("SummaryLoggerInstallerListener")),
                        HasPropertyWithValue.<Listener>hasProperty("stage", Is.is(Stage.install))
                ),
                AllOf.allOf(
                        HasPropertyWithValue.<Listener>hasProperty("classname", Is.is("RegistryInstallerListener")),
                        HasPropertyWithValue.<Listener>hasProperty("stage", Is.is(Stage.install))
                )
        ));

    }
}
