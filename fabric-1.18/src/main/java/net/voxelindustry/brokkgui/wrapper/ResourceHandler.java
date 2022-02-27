package net.voxelindustry.brokkgui.wrapper;

import net.voxelindustry.brokkgui.data.Resource;
import net.voxelindustry.brokkgui.internal.IResourceHandler;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ResourceHandler implements IResourceHandler
{
    @Override
    public boolean isTypeSupported(String resourceType)
    {
        switch (resourceType)
        {
            // LEGACY ONLY
            case "url":
                return true;
            case "assets":
                return true;
        }
        return false;
    }

    @Override
    public String readToString(Resource resource) throws IOException
    {
        if (resource.getType().equalsIgnoreCase("url") || resource.getType().equalsIgnoreCase("assets"))
        {
            String[] splitPath = resource.getPath().split(":");
            String domain = splitPath[0];
            String path = splitPath[1];
            return IOUtils.toString(ResourceHandler.class.getResourceAsStream("/assets/" + domain + "/" + path), StandardCharsets.UTF_8);
        }
        throw new UnsupportedOperationException("Unknown resource type used. type=" + resource.getType());
    }

    @Override
    public List<String> readToLines(Resource resource) throws IOException
    {
        if (resource.getType().equalsIgnoreCase("url") || resource.getType().equalsIgnoreCase("assets"))
        {
            String[] splitPath = resource.getPath().split(":");
            String domain = splitPath[0];
            String path = splitPath[1];
            return IOUtils.readLines(ResourceHandler.class.getResourceAsStream("/assets/" + domain + "/" + path), StandardCharsets.UTF_8);
        }
        throw new UnsupportedOperationException("Unknown resource type used. type=" + resource.getType());
    }
}
