package com.bh.spider.scheduler.component;

import com.bh.spider.common.component.Component;
import com.bh.spider.common.fetch.Contract;
import com.google.inject.Binding;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.spi.Elements;
import com.google.inject.util.Modules;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import javax.inject.Named;
import javax.inject.Singleton;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.stream.Collectors;

public class JarComponentRepository extends ComponentRepository {

    private JarComponentClassLoader classLoader;
    private Injector injector;

    public JarComponentRepository(Path path) throws IOException {
        super(Component.Type.JAR, path);

        this.injector = Guice.createInjector();


        this.classLoader = new JarComponentClassLoader(JarComponentRepository.class.getClassLoader());
        
        for (Component component : metadata().components())
            addComponentToClassLoader(component);


    }

    public ClassLoader classLoader() {
        return classLoader;
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return classLoader.loadClass(name);
    }

    @Override
    public Component save(byte[] data, String name, String description, boolean override) throws Exception {
        Component component = super.save(data, name, description, override);
        addComponentToClassLoader(component);
        return component;
    }


    private void addComponentToClassLoader(Component component) throws IOException {

        Path path = Paths.get(basePath().toString(), join(component.getName(), componentType()));


//        ClassLoader cl = URLClassLoader.newInstance(new URL[]{path.toUri().toURL()});
//
//
//        Collection<Class<?>> classes = scanClassesFromClassLoader(path, cl, Singleton.class, Named.class);
//
//        if (CollectionUtils.isNotEmpty(classes)) {
//            //todo 依赖注入,太过于难写 暂时搁置
//            bindToGuice(component, classes);
//        }


        this.classLoader.addJar(path);


    }

    private void deleteComponentFromClassLoader(Component component) throws IOException {
        Path path = Paths.get(basePath().toString(), component.getName());
        //todo 依赖注入,太过于难写 暂时搁置
//        this.unbindFromGuice(component, true);
        this.classLoader.removeJar(path);

    }

    @Override
    public void delete(String name) throws IOException {

        Component component = metadata().get(name);
        super.delete(name);
        deleteComponentFromClassLoader(component);
    }


    private Collection<Class<?>> scanClassesFromClassLoader(Path path, ClassLoader cl, Class<? extends Annotation>... annotations) throws IOException {
        JarFile jf = new JarFile(path.toFile());

        Manifest manifest = jf.getManifest();

        String scanPackage = manifest.getMainAttributes().getValue("Scan-Package");


        if (StringUtils.isNotBlank(scanPackage)) {

            ConfigurationBuilder builder = new ConfigurationBuilder()
                    .addClassLoader(cl)
                    .addUrls(ClasspathHelper.forPackage(scanPackage, cl))
                    .setExpandSuperTypes(false)
                    .setScanners(new SubTypesScanner(), new TypeAnnotationsScanner());


            Reflections reflections = new Reflections(builder);

            Set<Class<?>> classes = new HashSet<>();

            for (Class<? extends Annotation> annotation : annotations) {
                classes.addAll(reflections.getTypesAnnotatedWith(annotation));

            }
            return classes;
        }

        return null;

    }


    private void bindToGuice(Component component, Collection<Class<?>> classes) {


        Module module = binder -> {
            for (Class cls : classes) {

                Named named = (Named) cls.getAnnotation(Named.class);

                Singleton singleton = (Singleton) cls.getAnnotation(Singleton.class);

                if (named == null && singleton == null) continue;

                String name = named == null || StringUtils.isBlank(named.value()) ? cls.getName() : named.value();

                binder.bind(cls).annotatedWith(new GuiceName(component, name));

                Annotation annotation = cls.getAnnotation(Contract.class);
                if (annotation == null) continue;

                Contract contract = (Contract) annotation;


                Class<?> to = contract.implementFor() == Object.class ? cls : contract.implementFor();

                binder.bind(cls).annotatedWith(new GuiceName(component, name)).to(to);
            }
        };

        module = Modules.combine(unbindFromGuice(component, false), module);


        this.injector = this.injector.createChildInjector(module);
    }


    private Module unbindFromGuice(final Component component, boolean reset) {
        Collection<Binding<?>> bindings = this.injector.getBindings().values();


        List<Binding<?>> filted = bindings.stream().filter(binding -> {
            Annotation annotation = binding.getKey().getAnnotation();
            return !(annotation instanceof GuiceName && ((GuiceName) annotation).component() == component);
        }).collect(Collectors.toList());


        Module module = Elements.getModule(filted);

        if (reset)
            this.injector = Guice.createInjector(module);


        return module;

    }


}
