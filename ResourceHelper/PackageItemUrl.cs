using System;
using System.IO;

namespace ResourceHelper
{
    public abstract class PackageItemUrl
    {
        private readonly Uri _root;
        private readonly string _package;

        public Uri Root { get { return _root; } }

        public String Package { get { return _package; } }

        public abstract bool IsPackage();

        public PackageItemUrl(Uri root, string packageName)
        {
            if (null == root)
                throw new ArgumentNullException("root", "Directory root cannot be null");
            if (!root.IsAbsoluteUri)
                throw new ArgumentNullException("root", "Directory root cannot be null");
            _root = AssertValidAbsolutePackageUri(root, "root");
            if (string.IsNullOrEmpty(packageName))
                _package = "/";
            else
            {
                Uri uri = new Uri(_root, packageName);
                packageName = uri.ToString();
                if (packageName.Contains("?"))
                    throw new ArgumentException("Path name cannot contain a query string", "packageName");
                if (packageName.Contains("#"))
                    throw new ArgumentException("Path name cannot contain a fragment", "packageName");
                if (!_root.IsBaseOf(uri))
                    throw new ArgumentException("Package name is outside of the root directory", "packageName");
                _package = _root.MakeRelativeUri(uri).ToString();
            }
        }

        protected PackageItemUrl(PackageItemUrl url) {
            if (null == url)
                throw new ArgumentNullException();
            _root = url._root;
            _package = url._package;
        }
        
        public override int GetHashCode()
        {
            return ToString().GetHashCode();
        }

        public Uri GetFullPackageUri()
        {
            return (_package.Length == 1) ? _root : new Uri(_root, _package);
        }

        public abstract Uri GetFullResourceUri();

        public override string ToString()
        {
            return GetFullResourceUri().LocalPath;
        }

        public static Uri AssertValidAbsolutePackageUri(Uri uri, string argName)
        {
            string path = uri.ToString();
            if (path.Contains("?"))
                throw new ArgumentException("Path name cannot contain a query string", "path");
            if (path.Contains("#"))
                throw new ArgumentException("Path name cannot contain a fragment", "path");
            if (uri.Scheme != Uri.UriSchemeFile)
                throw new ArgumentException("Path is not a file system path", "path");
            if (path[path.Length - 1] == '/')
                return uri;
            return new Uri(path + "/", UriKind.Absolute);
        }

        public static Uri AssertValidAbsolutePackageUri(Uri uri)
        {
            return AssertValidAbsolutePackageUri(uri, "uri");
        }

        public static Uri ToAbsolutePackageUri(string path)
        {
            if (null == path)
                throw new ArgumentNullException("path", "Path cannot be null");
            if (path.Trim().Length == 0)
                throw new ArgumentException("Path name cannot be empty", "path");
            Uri uri;
            if (Uri.TryCreate(path, UriKind.Absolute, out uri))
                return AssertValidAbsolutePackageUri(uri, "path");

            if (Uri.TryCreate(path, UriKind.Relative, out uri))
                throw new ArgumentException("Path must be absolute", "path");
            throw new ArgumentException("Invalid path", "path");
        }

        private static Uri AssertValidAbsoluteFileUri(Uri uri, string argName)
        {
            string path = uri.ToString();
            if (path.Contains("?"))
                throw new ArgumentException("Path name cannot contain a query string", argName);
            if (path.Contains("#"))
                throw new ArgumentException("Path name cannot contain a fragment", argName);
            if (uri.Scheme != Uri.UriSchemeFile)
                throw new ArgumentException("Path is not a file system path", argName);
            while (path[path.Length - 1] == '/')
                uri = new Uri(path.Substring(0, path.Length - 1), UriKind.Absolute);
            FileInfo f = new FileInfo(uri.LocalPath);
            while (f.Directory != null)
            {
                if (f.Name.Length > 0)
                    return uri;
                f = new FileInfo(f.DirectoryName);
            }
            throw new ArgumentException("Path cannot be a filsystem root", argName);
        }

        public static Uri AssertValidAbsoluteFileUri(Uri uri)
        {
            if (null == uri)
                throw new ArgumentNullException("path", "Path cannot be null");
            if (!uri.IsAbsoluteUri)
                throw new ArgumentException("URL must be absolute", "path");
            return AssertValidAbsoluteFileUri(uri, "path");
        }

        public static Uri ToAbsoluteFileUri(string path)
        {
            if (null == path)
                throw new ArgumentNullException("path", "Path cannot be null");
            if (path.Trim().Length == 0)
                throw new ArgumentException("Path name cannot be empty", "path");
            Uri uri;
            if (Uri.TryCreate(path, UriKind.Absolute, out uri))
                return AssertValidAbsoluteFileUri(uri, "path");

            if (Uri.TryCreate(path, UriKind.Relative, out uri))
                throw new ArgumentException("Path must be absolute", "path");
            throw new ArgumentException("Invalid path", "path");
        }

    }
}