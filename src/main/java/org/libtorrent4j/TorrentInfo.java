/*
 * Copyright (c) 2018-2023, Alden Torres
 *
 * Licensed under the terms of the MIT license.
 * Copy of the license at https://opensource.org/licenses/MIT
 */

package org.libtorrent4j;

import org.libtorrent4j.swig.*;

import java.io.File;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the information stored in a .torrent file
 *
 * @author gubatron
 * @author aldenml
 */
public final class TorrentInfo {

    private final torrent_info ti;

    public TorrentInfo(torrent_info ti) {
        this.ti = ti;
    }

    /**
     * Load the torrent file and decode it inside the constructor, for convenience.
     * <p>
     * This might not be the most suitable for applications that
     * want to be able to report detailed errors on what might go wrong.
     *
     * @param torrent the torrent file
     */
    public TorrentInfo(File torrent) {
        this(bdecode0(torrent));
    }

    /**
     * Load the torrent data and decode it inside the constructor, for convenience.
     * <p>
     * This might not be the most suitable for applications that
     * want to be able to report detailed errors on what might go wrong.
     *
     * @param data the torrent data
     */
    public TorrentInfo(byte[] data) {
        this(bdecode0(data));
    }

    public TorrentInfo(MappedByteBuffer buffer) {
        try {
            long ptr = libtorrent_jni.directBufferAddress(buffer);
            long size = libtorrent_jni.directBufferCapacity(buffer);

            error_code ec = new error_code();
            this.ti = new torrent_info(ptr, (int) size, ec);

            if (ec.value() != 0) {
                throw new IllegalArgumentException("Can't decode data: " + ec.message());
            }
        } catch (Throwable e) {
            throw new IllegalArgumentException("Can't decode data mapped buffer: " + e.getMessage(), e);
        }
    }

    /**
     * @return the native object
     */
    public torrent_info swig() {
        return this.ti;
    }

    /**
     * The {@link FileStorage} object contains the information on
     * how to map the pieces to files.
     * <p>
     * It is separated from the {@link TorrentInfo} object because when creating torrents
     * a storage object needs to be created without having a torrent file. When renaming files
     * in a storage, the storage needs to make its own copy of the {@link FileStorage} in order
     * to make its mapping differ from the one in the torrent file.
     *
     * @return the files storage
     */
    public FileStorage files() {
        return new FileStorage(ti.files(), ti);
    }

    /**
     * Returns the original (unmodified) file storage for this torrent. This
     * is used by the web server connection, which needs to request files with the original
     * names. Filename may be changed using {@link #renameFile(int, String)}.
     *
     * @return the original file storage
     */
    public FileStorage origFiles() {
        return new FileStorage(ti.orig_files(), ti);
    }

    /**
     * Renames a the file with the specified index to the new name. The new
     * filename is reflected by the {@link FileStorage} returned by {@link #files()}
     * but not by the one returned by {@link #origFiles()}.
     * <p>
     * If you want to rename the base name of the torrent (for a multifile
     * torrent), you can copy the {@code FileStorage} (see {@link #files()} and
     * {@link #origFiles()} ), change the name, and then use
     * {@link #remapFiles(FileStorage)}.
     * <p>
     * The {@code newFilename} can both be a relative path, in which case the
     * file name is relative to the {@code savePath} of the torrent. If the
     * {@code newFilename} is an absolute path then the file is detached from
     * the {@code savePath} of the torrent. In this case the file is not moved when
     * {@link TorrentHandle#moveStorage(String, MoveFlags)} is invoked.
     *
     * @param index       the file index to rename
     * @param newFilename the new file name
     */
    public void renameFile(int index, String newFilename) {
        ti.rename_file(index, newFilename);
    }

    /**
     * Remaps the file storage to a new file layout. This can be used to, for
     * instance, download all data in a torrent to a single file, or to a
     * number of fixed size sector aligned files, regardless of the number
     * and sizes of the files in the torrent.
     * <p>
     * The new specified {@link FileStorage} must have the exact same size as
     * the current one.
     *
     * @param f the file storage
     */
    public void remapFiles(FileStorage f) {
        ti.remap_files(f.swig());
    }

    /**
     * This function is related to BEP38_ (mutable torrents). The
     * vector returned from this correspond to the "similar" in the
     * .torrent file. The info-hashes from within the info-dict
     * and from outside of it are included.
     * <p>
     * BEP38: http://www.bittorrent.org/beps/bep_0038.html
     *
     *
     */
    public ArrayList<Sha1Hash> similarTorrents() {
        return Sha1Hash.convert(ti.similar_torrents());
    }

    /**
     * This function is related to BEP38_ (mutable torrents). The
     * vector returned from this correspond to the "collections" keys
     * in the .torrent file. The collections from within the info-dict
     * and from outside of it are included.
     * <p>
     * BEP38: http://www.bittorrent.org/beps/bep_0038.html
     *
     *
     */
    public ArrayList<String> collections() {
        string_vector v = ti.collections();
        int size = (int) v.size();

        ArrayList<String> l = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            l.add(v.get(i));
        }

        return l;
    }

    /**
     * The total number of bytes the torrent-file represents (all the files in it).
     */
    public long totalSize() {
        return ti.total_size();
    }

    /**
     * Returns the sum of all non-pad file sizes. i.e. the files that will
     * actually be saved to disk by this torrent.
     */
    public long sizeOnDisk() {
        return ti.size_on_disk();
    }

    /**
     * The number of byte for each piece.
     * <p>
     * The difference between {@link #pieceSize(int)} and {@link #pieceLength()} is that
     * {@link #pieceSize(int)} takes the piece index as argument and gives you the exact size
     * of that piece. It will always be the same as {@link #pieceLength()} except in the case
     * of the last piece, which may be smaller.
     *
     *
     */
    public int pieceLength() {
        return ti.piece_length();
    }

    /**
     * The total number of pieces.
     *
     *
     */
    public int numPieces() {
        return ti.num_pieces();
    }

    /**
     * Returns the info-hash of the torrent.
     *
     * For BitTorrent v2 support, use `infoHash()` to get an object that
     * may hold both a v1 and v2 info-hash.
     */
    public Sha1Hash infoHash() {
        return new Sha1Hash(ti.info_hash());
    }

    /**
     * Returns an object that may hold both a v1 and v2 info-hash.
     */
    public InfoHash infoHashes() {
        return new InfoHash(ti.info_hashes());
    }

    /**
     * Returns whether this torrent has v1 metadata.
     *
     * Hybrid torrents have both. This is a shortcut for
     * `infoHashes().hasV1()`.
     */
    public boolean hasV1() {
        return ti.v1();
    }

    /**
     * Returns whether this torrent has v2 metadata.
     *
     * Hybrid torrents have both. This is a shortcut for
     * `infoHashes().hasV2()`.
     */
    public boolean hasV2() {
        return ti.v2();
    }

    /**
     * If you need index-access to files you can use this method
     * to access files using indices.
     */
    public int numFiles() {
        return ti.num_files();
    }

    /**
     * This function will map a piece index, a byte offset within that piece and
     * a size (in bytes) into the corresponding files with offsets where that data
     * for that piece is supposed to be stored.
     *
     * @param piece
     * @param offset
     * @param size
     *
     * @see FileSlice
     */
    public ArrayList<FileSlice> mapBlock(int piece, long offset, int size) {
        return FileStorage.mapBlock(ti.map_block(piece, offset, size));
    }

    /**
     * This function will map a range in a specific file into a range in the torrent.
     * The {@code offset} parameter is the offset in the file, given in bytes, where
     * 0 is the start of the file.
     * <p>
     * The input range is assumed to be valid within the torrent. {@code offset + size}
     * is not allowed to be greater than the file size. {@code index}
     * must refer to a valid file, i.e. it cannot be {@code >= numFiles()}.
     *
     * @param file
     * @param offset
     * @param size
     *
     * @see PeerRequest
     */
    public PeerRequest mapFile(int file, long offset, int size) {
        return new PeerRequest(ti.map_file(file, offset, size));
    }

    /**
     * Returns true if this torrent_info object has a torrent loaded.
     * <p>
     * This is primarily used to determine if a magnet link has had its
     * metadata resolved yet or not.
     *
     *
     */
    public boolean isValid() {
        return ti.is_valid();
    }

    /**
     * Returns true if this torrent is private. i.e., it should not be
     * distributed on the trackerless network (the kademlia DHT).
     *
     *
     */
    public boolean isPrivate() {
        return ti.priv();
    }

    /**
     * Returns true if this is an i2p torrent. This is determined by whether
     * or not it has a tracker whose URL domain name ends with ".i2p". i2p
     * torrents disable the DHT and local peer discovery as well as talking
     * to peers over anything other than the i2p network.
     *
     *
     */
    public boolean isI2p() {
        return ti.is_i2p();
    }

    public int pieceSize(int index) {
        return ti.piece_size(index);
    }

    /**
     * takes a piece-index and returns the 20-bytes sha1-hash for that
     * piece and ``info_hash()`` returns the 20-bytes sha1-hash for the info-section of the
     * torrent file.
     *
     * @param index
     *
     */
    public Sha1Hash hashForPiece(int index) {
        return new Sha1Hash(ti.hash_for_piece(index));
    }

    public boolean isLoaded() {
        return ti.is_loaded();
    }

    /**
     * returns the name of the torrent.
     * <p>
     * the name is an UTF-8 encoded strings.
     *
     *
     */
    public String name() {
        return ti.name();
    }

    /**
     * Returns the creation date of he torrent as time_t (`posix time`_).
     * If there's no time stamp in the torrent file,
     * a value of zero is returned.
     *
     * @return the time
     */
    public long creationDate() {
        return ti.creation_date();
    }

    /**
     * Returns the creator string in the torrent. If there is no creator string
     * it will return an empty string.
     *
     * @return the creator
     */
    public String creator() {
        return ti.creator();
    }

    /**
     * Returns the comment associated with the torrent. If there's no comment,
     * it will return an empty string.
     * <p>
     * The comment is an UTF-8 encoded strings.
     *
     * @return the comment
     */
    public String comment() {
        return ti.comment();
    }

    /**
     * If this torrent contains any DHT nodes, they are returned in
     * their original form (host name and port number).
     *
     *
     */
    public ArrayList<Pair<String, Integer>> nodes() {
        string_int_pair_vector v = ti.nodes();
        int size = (int) v.size();

        ArrayList<Pair<String, Integer>> l = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            string_int_pair p = v.get(i);
            l.add(new Pair<>(p.getFirst(), p.getSecond()));
        }

        return l;
    }

    /**
     * This is used when creating torrent. Use this to add a known DHT node.
     * It may be used, by the client, to bootstrap into the DHT network.
     *
     * @param host
     * @param port
     */
    public void addNode(String host, int port) {
        ti.add_node(new string_int_pair(host, port));
    }

    /**
     * This function looks up keys from the info-dictionary of the loaded
     * torrent file. It can be used to access extension values put in the
     * .torrent file. If the specified key cannot be found, it returns NULL.
     *
     * @param key
     *
     */
    public bdecode_node info(String key) {
        return ti.info(key);
    }

    /**
     * Clears the piece layers from the torrent_info. This is done by the
     * session when a torrent is added, to avoid storing it twice. The piece
     * layer (or other hashes part of the merkle tree) are stored in the
     * internal torrent object.
     */
    public void freePieceLayers() {
        ti.free_piece_layers();
    }

    /**
     * Generates a magnet URI from the specified torrent. If the torrent
     * is invalid, null is returned.
     * <p>
     * For more information about magnet links, see magnet-links_.
     */
    public String makeMagnetUri() {
        return ti.is_valid() ? libtorrent.make_magnet_uri(ti) : null;
    }

    public static TorrentInfo bdecode(byte[] data) {
        return new TorrentInfo(bdecode0(data));
    }

    private static torrent_info bdecode0(File file) {
        try {
            byte[] data = Files.bytes(file);
            return bdecode0(data);
        } catch (IOException e) {
            throw new IllegalArgumentException("Can't decode data from file: " + file, e);
        }
    }

    private static torrent_info bdecode0(byte[] data) {
        byte_vector buffer = Vectors.bytes2byte_vector(data);
        bdecode_node n = new bdecode_node();
        error_code ec = new error_code();
        int ret = bdecode_node.bdecode(buffer, n, ec);

        if (ret == 0) {
            ec.clear();
            torrent_info ti = new torrent_info(n, ec);
            buffer.clear(); // prevents GC
            if (ec.value() != 0) {
                throw new IllegalArgumentException("Can't decode data: " + ec.message());
            }
            return ti;
        } else {
            throw new IllegalArgumentException("Can't decode data: " + ec.message());
        }
    }
}
