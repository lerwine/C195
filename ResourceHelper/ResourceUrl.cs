using System;

namespace ResourceHelper
{

    public class ResourceUrl : PackageItemUrl, IEquatable<ResourceUrl>
    {
        private readonly string _fileName;

        public String Name { get { return _fileName; } }

        public override bool IsPackage()
        {
            return null == _fileName;
        }

        //internal ResourceUrl(ResourceUrl url, LanguageCode? code)
        internal ResourceUrl(ResourceUrl url) : base(url)
        {
            _fileName = url._fileName;
        }

        public ResourceUrl(Uri root, string packageName, string fileName) : base(root, packageName)
        {
            if (string.IsNullOrEmpty(fileName)) {
                _fileName = "";
                return;
            }
            Uri uri = AssertValidAbsoluteFileUri(new Uri(GetFullPackageUri(), fileName));
            if (root.IsBaseOf(uri)) {
                fileName = root.MakeRelativeUri(uri).ToString();
                if (!fileName.Contains("/")) {
                    _fileName = fileName;
                    return;
                }
            }
            throw new ArgumentException("File name must be a leaf");
        }

        public bool Equals(ResourceUrl other)
        {
            return null != other && (ReferenceEquals(this, other) ||
                (string.Equals(Package, other.Package, StringComparison.InvariantCultureIgnoreCase) &&
                ((null == Name) ? (null == other.Name) :
                (null != other.Name && string.Equals(Name, other.Name, StringComparison.InvariantCultureIgnoreCase)))));
        }

        public override bool Equals(object obj)
        {
            return null != obj && obj is ResourceUrl && Equals((ResourceUrl)obj);
        }

        public override Uri GetFullResourceUri()
        {
            if (_fileName == null)
                return GetFullPackageUri();
            return new Uri(GetFullPackageUri(), _fileName);
        }
    }
}