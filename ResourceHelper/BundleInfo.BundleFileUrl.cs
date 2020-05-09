using System;

namespace ResourceHelper
{
    public partial class BundleInfo
    {
        public class BundleFileUrl : PackageItemUrl
        {
            private readonly string _baseName;
            private readonly LanguageCode _code;
            private readonly long? _length;

            public long? Length { get { return _length; } }

            public LanguageCode Code { get { return _code; } }

            public string BaseName { get { return _baseName; } }

            internal BundleFileUrl(Uri root, string packageName, string baseName, LanguageCode code, long length) : base(root, packageName)
            {
                _baseName = baseName;
                _code = code;
                _length = length;
            }

            internal BundleFileUrl(BundleFileUrl url, long length) : base(url)
            {
                _baseName = url._baseName;
                _code = url._code;
                _length = length;
            }

            internal BundleFileUrl(BundleFileUrl url, LanguageCode code) : base(url)
            {
                _baseName = url._baseName;
                _code = code;
                _length = null;
            }

            public override bool IsPackage()
            {
                return false;
            }

            public override Uri GetFullResourceUri()
            {
                return new Uri(GetFullPackageUri(), _baseName + "_" + _code.ToString() + ".properties");
            }
        }
    }
}